package com.evanram.voip.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.evanram.voip.AudioManager;
import com.evanram.voip.CallData;
import com.evanram.voip.VoIPApplication;

public class UDPClient extends Client
{
	public UDPClient(InetAddress peerIp, int peerPort, AudioManager am)
	{
		super(peerIp, peerPort, am);
	}

	@Override
	public void enterClientLoop()
	{
		//TODO keep-alive packets to make sure that the other end is still alive

		try
		{
			while(running) // when keep-alive is implemented: while(running && keptAlive) ...
			{
				sendPacket(am.read()); //send next read audio bytes to server
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			VoIPApplication.instance.endCall();
		}
	}

	private void sendPacket(byte[] data) throws IOException
	{
		DatagramSocket socket = new DatagramSocket();
		socket.send(new DatagramPacket(data, data.length, peerIp, peerPort));
		socket.close();
	}

	@Override
	public void implementedStopClient() throws IOException
	{
		sendPacket(CallData.END_CALL);
	}
}
