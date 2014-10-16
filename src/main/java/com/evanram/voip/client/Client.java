package com.evanram.voip.client;

import static com.evanram.voip.VoIPApplication.AUDIO_FORMAT;

import java.io.IOException;
import java.net.InetAddress;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public abstract class Client extends Thread
{
	private static final int DATALINE_BUFFER_SIZE = 1024;
	private static final DataLine.Info DATALINE = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT, DATALINE_BUFFER_SIZE);
	
	//must be static or line unavailable exception thrown (even if we close() it first)
	//possibly due to line listeners getting attached at the start of each call
	private static TargetDataLine targetDataLine;
	
	protected final InetAddress peerIp;
	protected final int peerPort;
	protected volatile boolean running;
	
	public Client(InetAddress peerIp, int peerPort)
	{
		setName("Client");
		this.peerIp = peerIp;
		this.peerPort = peerPort;
	}
	
	@Override
	public final void run()
	{
		if(running)
			throw new IllegalStateException(
					new StringBuilder().append("Client ").append(getClass().getSimpleName()).append(" already running").toString());
		
		running = true;
		enterClientLoop();
		running = false;
	}
	
	public abstract void enterClientLoop();
	public abstract void implementedStopClient() throws IOException;
	
	public final void stopClient()
	{
		System.out.println("Stopping client");
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
	
	protected final TargetDataLine setupTargetDataLine()
	{
		if(targetDataLine != null)
			return targetDataLine;
		
		try
		{
			targetDataLine = (TargetDataLine) AudioSystem.getLine(DATALINE);
			targetDataLine.open(AUDIO_FORMAT);
			targetDataLine.start();
			
			return targetDataLine;
		}
		catch(LineUnavailableException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public boolean isRunning()
	{
		return running;
	}
}
