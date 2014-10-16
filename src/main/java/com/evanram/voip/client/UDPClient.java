package com.evanram.voip.client;

import static com.evanram.voip.VoIPApplication.bufferSize;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.TargetDataLine;

import com.evanram.voip.CallData;


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
		
		while(running/*&& keptAlive*/)
		{
			byte[] buffer = new byte[bufferSize];
			targetDataLine.read(buffer, 0, buffer.length);
			sendPacket(buffer);
		}
	}

	private void sendPacket(byte[] data)
	{
		try
		{
			new DatagramSocket().send(new DatagramPacket(data, data.length, peerIp, peerPort));
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.err.println("Error in sending packet over UDP datagram socket");
		}
	}
	
	@Override
	public void implementedStopClient() throws IOException
	{
		sendPacket(CallData.END_CALL);
	}
}
