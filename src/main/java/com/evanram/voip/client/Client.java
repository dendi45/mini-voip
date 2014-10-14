package com.evanram.voip.client;

import static com.evanram.voip.VoIPApplication.AUDIO_FORMAT;

import java.net.InetAddress;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public abstract class Client extends Thread
{
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
	public void run()
	{
		if(running)
			throw new IllegalStateException(
					new StringBuilder().append("Client ").append(getClass().getSimpleName()).append(" already running").toString());
		
		running = true;
		enterClientLoop();
		running = false;
	}
	
	public abstract void enterClientLoop();
	
	protected TargetDataLine setupTargetDataLine()
	{
		try
		{
			//TODO possibly change 1024 to BUFFER_SIZE.
			DataLine.Info dataLine = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT, 1024);
			TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLine);
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
}
