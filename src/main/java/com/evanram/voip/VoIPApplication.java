package com.evanram.voip;

import static com.evanram.voip.Utils.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import com.evanram.voip.client.Client;
import com.evanram.voip.client.TCPClient;
import com.evanram.voip.client.UDPClient;
import com.evanram.voip.gui.Gui;
import com.evanram.voip.server.Server;
import com.evanram.voip.server.TCPServer;
import com.evanram.voip.server.UDPServer;

public class VoIPApplication
{
	//16k default buffer and sample rate work, as far as my tests have gone, well in terms of call quality.
	public static final AudioFormat AUDIO_FORMAT = new AudioFormat(16_000F, 16, 2, true, false);
	public static int bufferSize = 16_000;
	
	public static VoIPApplication instance;	//singleton since this class should never be created more than once

	private Gui gui;
	private Server server;
	private Client client;
	private InetAddress peerIp;
	private int peerPort;
	private Scanner scanner;
	
	private volatile long callStartTimeMillis;
	
	private VoIPApplication() {}	//disallow accidentally instantiating from elsewhere
	
	public static void main(String[] args)
	{
		System.out.println("Starting p2p VoIP. Note any two peers must share the same network protocol");
		instance = new VoIPApplication();
		instance.setGui(new Gui());
	}
	
	public static void playSound(byte[] buffer)
	{
		if(isMostlyQuiet(buffer))
			return;
		
		try
		{
			final Clip clip = AudioSystem.getClip();

			clip.addLineListener(new LineListener()
			{
				@Override
				public void update(LineEvent event)
				{
					if(event.getType() == LineEvent.Type.STOP)
						clip.close();
				}
			});
			
			clip.open(AUDIO_FORMAT, buffer, 0, buffer.length);
			clip.start();
		}
		catch(LineUnavailableException e)
		{
			e.printStackTrace();
			instance.shutdown();
		}
	}
	
	//TODO scanner -> gui input
	public void start()
	{
		try
		{
			if(scanner == null)
				scanner = new Scanner(System.in);
			
			System.out.print("Network protocol (0 = UDP, 1 = TCP): ");
			int protocol = Integer.parseInt(getOrDefault(scanner.nextLine(), "0"));
			System.out.print("Server listen port: ");
			int serverPort = Integer.parseInt(getOrDefault(scanner.nextLine(), "38936"));
			System.out.print("Peer address: ");
			InetAddress peerIp = InetAddress.getByName(getOrDefault(scanner.nextLine(), "127.0.0.1"));
			System.out.print("Peer port: ");
			int peerPort = Integer.parseInt(getOrDefault(scanner.nextLine(), "38936"));
			
			if(peerIp == InetAddress.getLoopbackAddress() && peerPort == serverPort)
				System.out.println("Echo mode detected (peer is localhost & peer and server ports are equal)");
			
			String line = "|>------------------------------------<|";
			System.out.println(line + '\n' +formSettingsJSON(protocol, serverPort, peerIp, peerPort) + '\n' + line);
			
			this.peerIp = peerIp;
			this.peerPort = peerPort;
			
			server = (protocol == 0 ? new UDPServer(serverPort) : new TCPServer(serverPort));
			server.start();
			
			client = (protocol == 0 ? new UDPClient(peerIp, peerPort) : new TCPClient(peerIp, peerPort));
			client.start();
			
			callStartTimeMillis = System.currentTimeMillis();
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
		}
	}
	
	public void shutdown()
	{
		try
		{
			endCall();
		}
		finally
		{
			System.exit(0);
		}
	}
	
	public void endCall()
	{
		System.out.println("Ending call");
		peerIp = null;
		peerPort = 0;
		
		if(client != null)
			client.stopClient();
		
		if(server != null)
			server.stopServer();
		
		System.out.println("Call ended");
	}
	
	public String getCallInfo()
	{
		return new StringBuilder().append(peerIp.getHostName()).append(" - ").append(getCallTime()).toString();
	}
	
	private String getCallTime()
	{
		long delta = System.currentTimeMillis() - callStartTimeMillis;
		
		int seconds = (int) ((delta / 1000) % 60);
		int minutes = (int) ((delta / (1000*60)) % 60);
		int hours = (int) ((delta / (1000*60*60)) % 24);
		
		return String.format("%d:%02d:%02d", hours, minutes, seconds);
	}

	public Gui getGui()
	{
		return gui;
	}

	public void setGui(Gui gui)
	{
		this.gui = gui;
	}

	public boolean isInCall()
	{
		return peerIp != null && client.isRunning() && server.isRunning();
	}
}
