package com.evanram.voip.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.evanram.voip.AudioManager;
import com.evanram.voip.VoIPApplication;

public class UDPServer extends Server
{
	private DatagramSocket serverDatagramSocket;
	
	public UDPServer(int port, AudioManager am)
	{
		super(port, am);
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
				int bufferSize = am.getBufferSize();
				DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
				serverDatagramSocket.receive(packet);
				
				handleReceivedBytes(packet.getData());
			}
		}
		catch(SocketException e)
		{
			System.err.println(e.getMessage());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			VoIPApplication.instance.endCall();
		}
	}

	@Override
	public void implementedStopServer() throws IOException
	{
		serverDatagramSocket.close();
	}
}
