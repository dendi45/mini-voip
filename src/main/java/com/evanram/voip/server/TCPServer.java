package com.evanram.voip.server;

import static com.evanram.voip.VoIPApplication.bufferSize;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.evanram.voip.Utils;

public class TCPServer extends Server
{
	private ServerSocket serverSocket;
	private Socket remoteSocket;
	private DataInputStream remoteSocketInputStream;
	
	public TCPServer(int port)
	{
		super(port);
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
				byte[] buffer = new byte[bufferSize];
				remoteSocketInputStream.read(buffer, 0, buffer.length);
				handle(buffer);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void implementedStopServer() throws IOException
	{
		serverSocket.close();
	}
}
