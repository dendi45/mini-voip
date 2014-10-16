package com.evanram.voip.server;

import static com.evanram.voip.VoIPApplication.bufferSize;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer extends Server
{
	private DatagramSocket serverDatagramSocket;
	
	public UDPServer(int port)
	{
		super(port);
	}
	
	@Override
	public void enterServerLoop()
	{
		try
		{
			serverDatagramSocket = new DatagramSocket(port);
			System.out.println("Server datagram socket created");
			
			while(running)
			{
				DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
				serverDatagramSocket.receive(packet);
				//for debugging: printPacket(packet);
				byte[] buffer = packet.getData();
				handle(buffer);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void implementedStopServer() throws IOException
	{
		serverDatagramSocket.close();
	}
}
