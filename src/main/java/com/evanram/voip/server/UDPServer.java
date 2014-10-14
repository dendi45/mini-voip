package com.evanram.voip.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static com.evanram.voip.VoIPApplication.*;

public class UDPServer extends Server
{
	private DatagramSocket socket;
	
	public UDPServer(int port)
	{
		super(port);
	}
	
	@Override
	public void enterServerLoop()
	{
		try
		{
			socket = new DatagramSocket(port);
			
			while(running)
			{
				DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
				socket.receive(packet);

//				System.out.println("packet: ["
//						+ "len=" + packet.getLength() + 
//						", offset=" + packet.getOffset() + 
//						", port=" + packet.getPort() + 
//						", ip=" + packet.getAddress().getHostAddress() + "]");
				
				byte[] buffer = packet.getData();
				playSound(buffer);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
