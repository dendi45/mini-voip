package com.evanram.voip.server;

public abstract class Server extends Thread
{
	protected final int port;
	protected volatile boolean running;
	
	public Server(int port)
	{
		setName("Listen Server");
		this.port = port;
	}
	
	public void run()
	{
		if(running)
			throw new IllegalStateException(
					new StringBuilder().append("Server ").append(getClass().getSimpleName()).append(" already running").toString());
		
		running = true;
		enterServerLoop();
		running = false;
	}
	
	public abstract void enterServerLoop();
}
