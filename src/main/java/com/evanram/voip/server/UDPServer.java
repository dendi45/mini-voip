package com.evanram.voip.server;

import static com.evanram.voip.VoIPApplication.BUFFER_SIZE;
import static com.evanram.voip.VoIPApplication.playSound;

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
				DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
				serverDatagramSocket.receive(packet);
				//for debugging: printPacket(packet);
				byte[] buffer = packet.getData();
				playSound(buffer);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void printPacket(DatagramPacket packet)
	{
		//ex. 'packet: [len=16000, offset=0, port=50028, ip=127.0.0.1]'
		System.out.println("packet: ["
				+ "len=" + packet.getLength() + 
				", offset=" + packet.getOffset() + 
				", port=" + packet.getPort() + 
				", ip=" + packet.getAddress().getHostAddress() + "]");
	}
}
