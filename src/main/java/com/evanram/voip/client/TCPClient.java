package com.evanram.voip.client;

import static com.evanram.voip.VoIPApplication.bufferSize;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.sound.sampled.TargetDataLine;

import com.evanram.voip.CallData;
import com.evanram.voip.Utils;

public class TCPClient extends Client
{
	private DataOutputStream out;
	
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
			
			out = new DataOutputStream(socket.getOutputStream());
			out.flush();
			
			while(running && Utils.tcpSocketOK(socket))
			{
				byte[] buffer = new byte[bufferSize];
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

	@Override
	public void implementedStopClient() throws IOException
	{
		out.write(CallData.END_CALL);
		out.flush();
	}
}
