package com.evanram.voip.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.evanram.voip.AudioManager;
import com.evanram.voip.Utils;
import com.evanram.voip.VoIPApplication;

public class TCPServer extends Server
{
	private ServerSocket serverSocket;
	private Socket remoteSocket;
	private DataInputStream remoteSocketInputStream;
	
	public TCPServer(int port, AudioManager am)
	{
		super(port, am);
	}
	
	@Override
	public void enterServerLoop()
	{
		try
		{
			serverSocket = new ServerSocket(port);
			remoteSocket = serverSocket.accept();
			remoteSocketInputStream = new DataInputStream(remoteSocket.getInputStream());
			System.out.println("TCP server accepted and found input stream for socket: " + remoteSocket.toString());

			while(running && Utils.tcpSocketOK(remoteSocket))
			{
				byte[] buffer = new byte[am.getBufferSize()];
				remoteSocketInputStream.read(buffer, 0, buffer.length);
				handleReceivedBytes(buffer);
			}
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
		serverSocket.close();
	}
}
