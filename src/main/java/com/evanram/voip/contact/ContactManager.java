package com.evanram.voip.contact;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ContactManager
{
	private List<Contact> contacts = new ArrayList<>();
	private Contact latestContact;
	
	public List<Contact> getContacts()
	{
		try
		{
			for(int i =0;i<10;i++)
			contacts.add(new Contact(Integer.toString((int) (Math.random() * 100000)) + "-" + Integer.toString((int) (Math.random() * 100000)), InetAddress.getLocalHost(), 38936));
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return contacts;
	}
	
	public Contact get(String name)
	{
		for(Contact contact : contacts)
		{
			if(contact.getName().equalsIgnoreCase(name))
				return contact;
		}
		
		return null;
	}
	
	public Contact getLatestContact()
	{
		return latestContact;
	}
	
	public void setLatestContact(Contact latestContact)
	{
		this.latestContact = latestContact;
	}
}
