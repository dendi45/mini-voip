package com.evanram.voip.client;

import static com.evanram.voip.VoIPApplication.BUFFER_SIZE;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.sound.sampled.TargetDataLine;

public class TCPClient extends Client
{
	public TCPClient(InetAddress peerIp, int peerPort)
	{
		super(peerIp, peerPort);
	}

	@Override
	public void enterClientLoop()
	{
		try(Socket socket = new Socket(peerIp, peerPort))
		{
			TargetDataLine targetDataLine = setupTargetDataLine();
			
			if(targetDataLine == null)
				return;
			
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.flush();
			
			while(running && (socket != null && socket.isConnected() && !socket.isClosed()))
			{
				byte[] buffer = new byte[BUFFER_SIZE];
				targetDataLine.read(buffer, 0, buffer.length);

				out.write(buffer);
				out.flush();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.err.println("Error in establishing connection to peer socket");
		}
	}
}
