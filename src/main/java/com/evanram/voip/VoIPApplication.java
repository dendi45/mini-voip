package com.evanram.voip;

import static com.evanram.voip.Utils.formSettingsJSON;
import static com.evanram.voip.Utils.getOrDefault;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.evanram.voip.client.Client;
import com.evanram.voip.client.TCPClient;
import com.evanram.voip.client.UDPClient;
import com.evanram.voip.contact.Contact;
import com.evanram.voip.contact.ContactManager;
import com.evanram.voip.gui.Gui;
import com.evanram.voip.server.Server;
import com.evanram.voip.server.TCPServer;
import com.evanram.voip.server.UDPServer;

public class VoIPApplication
{
	public static final int PROTOCOL_UDP = 0;
	public static final int PROTOCOL_TCP = 1;
	
	public static VoIPApplication instance;	//singleton since this class should never be created more than once
	public static ContactManager contactManager = new ContactManager();

	private Gui gui;
	private AudioManager am;
	private Server server;
	private Client client;
	private InetAddress peerAddress;
	private Scanner scanner;
	private boolean noguiMode = false;
	private volatile long callStartTimeMillis;
	
	private VoIPApplication() {}	//disallow instantiation from other classes
	
	public static void main(String[] args)
	{
		System.out.println("Starting p2p VoIP. Note any two peers must share the same network protocol");
		instance = new VoIPApplication();
		
		if(args.length > 0 && args[0].equalsIgnoreCase("-nogui"))
		{
			instance.noguiMode = true;
			instance.start(null);
		}
		else
			instance.setGui(new Gui());
	}
	
	public void start(Contact contact)
	{
		if(am == null)
			am = new AudioManager(AudioManager.DEFAULT_BUFFER_SIZE);	//initialize audio manager
		else
			am.update();	//update objects in audio manager
		
		contactManager.setLatestContact(contact);
		
		try
		{
			int protocol, serverPort, peerPort;
			InetAddress peerAddress;
			
			if(this.noguiMode)	
			{
				if(scanner == null)
					scanner = new Scanner(System.in);
				
				System.out.print("Network protocol (0 = UDP, 1 = TCP): ");
				protocol = Integer.parseInt(getOrDefault(scanner.nextLine(), "0"));
				System.out.print("Server listen port: ");
				serverPort = Integer.parseInt(getOrDefault(scanner.nextLine(), "38936"));
				System.out.print("Peer address: ");
				peerAddress = InetAddress.getByName(getOrDefault(scanner.nextLine(), "127.0.0.1"));
				System.out.print("Peer port: ");
				peerPort = Integer.parseInt(getOrDefault(scanner.nextLine(), "38936"));
			}
			else
			{
				protocol = PROTOCOL_UDP;
				serverPort = 38936;
				peerPort = contact.getPort();
				peerAddress = contact.getAddress();
			}
			
			if(peerAddress == InetAddress.getLoopbackAddress() && peerPort == serverPort)
				System.out.println("Echo mode detected (peer is localhost & peer and server ports are equal)");
			
			System.out.println("Starting call with settings: \n" + formSettingsJSON(protocol, serverPort, peerAddress, peerPort));
			
			this.peerAddress = peerAddress;
			
			server = (protocol == 0 ? new UDPServer(serverPort, am) : new TCPServer(serverPort, am));
			server.start();
			
			client = (protocol == 0 ? new UDPClient(peerAddress, peerPort, am) : new TCPClient(peerAddress, peerPort, am));
			client.start();
			
			callStartTimeMillis = System.currentTimeMillis();
		}
		catch(UnknownHostException e)
		{
			System.err.println(e.getMessage());
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			endCall();
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
		peerAddress = null;
		
		try
		{
			if(client != null)
				client.stopClient();
			
			if(server != null)
				server.stopServer();
		}
		finally
		{
			client = null;
			server = null;
		}
		
		System.out.println("Call ended");
	}
	
	public String getCallInfo()
	{
		Contact contact = contactManager.getLatestContact();
		String name = contact.getTruncatedEllipsisName(gui.getWidth() / 2);	//this is a good dynamic size to have before limiting name
		return new StringBuilder().append(name).append(" - ").append(getCallTime()).toString();
	}
	
	public String getCallTime()
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

	public AudioManager getAudioManager()
	{
		return am;
	}
	
	public boolean isInCall()
	{
		return peerAddress != null && client.isRunning() && server.isRunning();
	}
}
