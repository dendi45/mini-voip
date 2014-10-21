package com.evanram.voip.client;

import java.io.IOException;
import java.net.InetAddress;

import com.evanram.voip.AudioManager;

public abstract class Client extends Thread
{
	protected final InetAddress peerIp;
	protected final int peerPort;
	protected AudioManager am;
	protected volatile boolean running;

	public Client(InetAddress peerIp, int peerPort, AudioManager am)
	{
		setName("Client");
		this.peerIp = peerIp;
		this.peerPort = peerPort;
		this.am = am;
	}

	@Override
	public final void run()
	{
		if(running)
			throw new IllegalStateException(new StringBuilder().append("Client ").append(getClass().getSimpleName()).append(" already running").toString());

		running = true;
		enterClientLoop();
		running = false;
	}

	public abstract void enterClientLoop();

	public abstract void implementedStopClient() throws IOException;

	public final void stopClient()
	{
		running = false;

		try
		{
			implementedStopClient();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("Client stopped");
	}

	public boolean isRunning()
	{
		return running;
	}
}
