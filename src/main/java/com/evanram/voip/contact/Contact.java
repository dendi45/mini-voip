package com.evanram.voip.contact;

import java.net.InetAddress;

import com.evanram.voip.Utils;

public class Contact
{
	private String name;
	private InetAddress address;
	private int port;
	private String cachedTruncatedEllipsisName = null;
	
	public Contact(String name, InetAddress address, int port)
	{
		this.name = name;
		this.address = address;
		this.port = port;
	}

	public String getName()
	{
		return name;
	}

	public InetAddress getAddress()
	{
		return address;
	}

	public int getPort()
	{
		return port;
	}

	public void setName(String name)
	{
		if(!this.name.equals(name))
			cachedTruncatedEllipsisName = null;
		
		this.name = name;
	}

	public void setAddress(InetAddress address)
	{
		this.address = address;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
	
	public String getTruncatedEllipsisName(int maxWidth)
	{
		if(cachedTruncatedEllipsisName != null)	//caching so that stringWidth calls are not constantly made.
			return cachedTruncatedEllipsisName;
		
		String newName = name;
		int width = Utils.stringWidth(newName);
		int substringPos;
		for(substringPos = newName.length(); width > maxWidth && width != 0; substringPos--)
			width = Utils.stringWidth(newName = newName.substring(0, substringPos));
		
		if(!newName.equals(name))
			newName += "...";

		return (cachedTruncatedEllipsisName = newName);
	}
}
