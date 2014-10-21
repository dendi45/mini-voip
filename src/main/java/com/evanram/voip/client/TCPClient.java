package com.evanram.voip.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import com.evanram.voip.AudioManager;
import com.evanram.voip.CallData;
import com.evanram.voip.Utils;
import com.evanram.voip.VoIPApplication;

public class TCPClient extends Client
{
	private DataOutputStream out;

	public TCPClient(InetAddress peerIp, int peerPort, AudioManager am)
	{
		super(peerIp, peerPort, am);
	}

	@Override
	public void enterClientLoop()
	{
		try(Socket socket = new Socket(peerIp, peerPort))
		{
			out = new DataOutputStream(socket.getOutputStream());
			out.flush();

			while(running && Utils.tcpSocketOK(socket))
			{
				out.write(am.read()); //send next read audio bytes to server
				out.flush();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			VoIPApplication.instance.endCall();
		}
	}

	@Override
	public void implementedStopClient() throws IOException
	{
		out.write(CallData.END_CALL);
		out.flush();
	}
}
