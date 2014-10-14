package com.evanram.voip.client;

import static com.evanram.voip.VoIPApplication.BUFFER_SIZE;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.TargetDataLine;


public class UDPClient extends Client
{
	public UDPClient(InetAddress peerIp, int peerPort)
	{
		super(peerIp, peerPort);
	}

	@Override
	public void enterClientLoop()
	{
		TargetDataLine targetDataLine = setupTargetDataLine();
		
		if(targetDataLine == null)
			return;
		
		//TODO keep-alive packets to make sure that the other end is still alive
		//XXX boolean keptAlive = true;
		
		while(running/*XXX && keptAlive*/)
		{
			//---------------------------
			//XXX keptAlive = /*ping ping ping response response response, or a keep-alive thread*/
			//---------------------------
			
			byte[] buffer = new byte[BUFFER_SIZE];
			
			//XXX probably invoke a thread to check keep alive
			targetDataLine.read(buffer, 0, buffer.length);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, peerIp, peerPort);
			
			try
			{
				new DatagramSocket().send(packet);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.err.println("Error in sending packet over UDP datagram socket");
			}
		}
	}
}
