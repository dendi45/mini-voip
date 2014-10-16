package com.evanram.voip.server;

import static com.evanram.voip.CallData.END_CALL;
import static com.evanram.voip.VoIPApplication.*;

import java.io.IOException;
import java.util.Arrays;


public abstract class Server extends Thread
{
	protected final int port;
	protected volatile boolean running;
	
	public Server(int port)
	{
		setName("Listen Server");
		this.port = port;
	}
	
	@Override
	public final void run()
	{
		if(running)
			throw new IllegalStateException(
					new StringBuilder().append("Server ").append(getClass().getSimpleName()).append(" already running").toString());
		
		running = true;
		enterServerLoop();
		running = false;
	}
	
	public abstract void enterServerLoop();
	public abstract void implementedStopServer() throws IOException;
	
	public void handle(byte[] bytes)
	{
		int length = bytes.length;
		
		if(length == bufferSize)
		{
			playSound(bytes);
		}
		else
		{
			if(Arrays.equals(bytes, END_CALL))
			{
				instance.endCall();
			}
		}
	}
	
	public final void stopServer()
	{
		System.out.println("Stopping server");
		running = false;
		
		try
		{
			implementedStopServer();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Server stopped");
	}

	public boolean isRunning()
	{
		return running;
	}
}
