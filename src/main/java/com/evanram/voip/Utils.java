package com.evanram.voip;

import java.awt.Point;
import java.net.InetAddress;
import java.net.Socket;
import java.text.MessageFormat;

import com.evanram.voip.gui.Gui;

public class Utils
{
	public static boolean tcpSocketOK(Socket socket)
	{
		return (socket != null && socket.isConnected() && !socket.isClosed());
	}

	public static boolean isMostlyQuiet(byte[] buffer)
	{
		//referenced from http://stackoverflow.com/a/15010203

		float hold = .9999F;
		float thresh = .7F;
		float soundEnvelope = 0;

		float quietBytes = 0;

		for(int i = 0; i < buffer.length; i++)
		{
			float currentByte = buffer[i];

			float f = Math.abs(currentByte);
			soundEnvelope = soundEnvelope * hold + f * (1 - hold);

			if(soundEnvelope <= thresh)
				quietBytes++;
		}

		float level = (quietBytes / (float) buffer.length); //determine how much of this buffer was quiet
		return level > 0.35F; //magic value 0.35F seems to work well
	}

	public static String getOrDefault(String input, String defaultOutput)
	{
		if(input == null || (input = input.trim()).length() == 0)
			return defaultOutput;

		return input;
	}

	public static String formSettingsJSON(int protocol, int serverPort, InetAddress peerIp, int peerPort)
	{
		return MessageFormat.format("\"settings\": '{'" + "{0}\"protocol\": \"{1}\"" + "{0}\"listen_port\": \"{2}\"" + "{0}\"peer_host\": \"{3}\""
				+ "{0}\"peer_port\": \"{4}\"" + "\n'}'",
		//Using Integer.toString because MessageFormat.format will change an int such as 12345 to 12,345
				"\n\t", protocol, Integer.toString(serverPort), peerIp.getHostName(), Integer.toString(peerPort));
	}

	public static int stringWidth(String s)
	{
		return Gui.staticCacheFontMetrics.stringWidth(s);
	}

	public static Point getNearestPoint(Point testPos, Point[] reference)
	{
		if(reference.length == 0)
			return testPos;

		double bestDistance = testPos.distance(reference[0]);
		Point bestPoint = reference[0];

		for(int i = 1; i < reference.length; i++)
		{
			double d = testPos.distance(reference[i]);
			if(d < bestDistance)
			{
				bestDistance = d;
				bestPoint = reference[i];
			}
		}

		return bestPoint;
	}
}
