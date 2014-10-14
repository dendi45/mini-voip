package com.evanram.voip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
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
import com.evanram.voip.server.Server;
import com.evanram.voip.server.TCPServer;
import com.evanram.voip.server.UDPServer;

public class VoIPApplication
{
	//16k buffer and sample rate work, as far as my tests have gone, well in terms of call quality.
	public static final int BUFFER_SIZE = 16_000;
	public static final AudioFormat AUDIO_FORMAT = new AudioFormat(16_000F, 16, 2, true, false);
	
	public static VoIPApplication instance;	//TODO might not want to use a singleton

	private Server server;
	private Client client;
	
	public static void main(String[] args)
	{
		//TODO launch GUI used to launch this
		instance = new VoIPApplication();
		instance.start();
	}
	
	public void start()
	{
		try
		{
			System.out.println("Starting p2p VoIP. Note any two peers must share the same network protocol");
			
			Scanner scanner = new Scanner(System.in);
			System.out.print("Network protocol (0 = UDP, 1 = TCP): ");
			int protocol = Integer.parseInt(getOrDefault(scanner.nextLine(), "0"));
			System.out.print("Server listen port: ");
			int serverPort = Integer.parseInt(getOrDefault(scanner.nextLine(), "38936"));
			System.out.print("Peer address: ");
			InetAddress peerIp = InetAddress.getByName(getOrDefault(scanner.nextLine(), "127.0.0.1"));
			System.out.print("Peer port: ");
			int peerPort = Integer.parseInt(getOrDefault(scanner.nextLine(), "38936"));
			scanner.close();
			
			if(peerIp == InetAddress.getLoopbackAddress() && peerPort == serverPort)
				System.out.println("Echo mode detected (peer is localhost & peer and server ports are equal)");
			
			String line = "|>------------------------------------<|";
			System.out.println(line + '\n' +formSettingsJSON(protocol, serverPort, peerIp, peerPort) + '\n' + line);
			
			server = (protocol == 0 ? new UDPServer(serverPort) : new TCPServer(serverPort));
			server.start();
			
			client = (protocol == 0 ? new UDPClient(peerIp, peerPort) : new TCPClient(peerIp, peerPort));
			client.start();
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
		}
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
			//TODO end call and then call System.exit(1)
		}
	}
	
	private static boolean isMostlyQuiet(byte[] buffer)
	{
		//referenced from http://stackoverflow.com/a/15010203
		
		float hold = .9999F;
		float thresh = .7F;
		float soundEnvelope = 0;

		float quietBytes = 0;
		
		for (int i = 0; i < buffer.length; i++)
		{
			float currentByte = buffer[i];
			
			float f = Math.abs(currentByte);
			soundEnvelope = soundEnvelope * hold + f * (1 - hold);

			if (soundEnvelope <= thresh)
				quietBytes++;
		}
		
		float level = (quietBytes / (float) buffer.length);	//determine how much of this buffer was quiet
		return level > 0.35F;	//magic value 0.35F seems to work well
	}
	
	private static String getOrDefault(String input, String defaultOutput)
	{
		if(input == null || (input = input.trim()).length() == 0)
			return defaultOutput;
		
		return input;
	}
	
	private static String formSettingsJSON(int protocol, int serverPort, InetAddress peerIp, int peerPort)
	{
		return MessageFormat.format(
				"\"settings\": '{'"
					+ "{0}\"protocol\": \"{1}\""
					+ "{0}\"listen_port\": \"{2}\""
					+ "{0}\"peer_host\": \"{3}\""
					+ "{0}\"peer_port\": \"{4}\""
				+ "\n'}'", 
				//Using Integer.toString because MessageFormat.format will change an int such as 12345 to 12,345
				"\n\t", protocol, Integer.toString(serverPort), peerIp.getHostName(), Integer.toString(peerPort));
	}
}
