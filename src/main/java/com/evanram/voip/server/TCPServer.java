package com.evanram.voip.server;


public class TCPServer extends Server
{
	public TCPServer(int port)
	{
		super(port);
	}
	
	@Override
	public void enterServerLoop()
	{
		throw new UnsupportedOperationException("No current support for TCP server");
	}
}
