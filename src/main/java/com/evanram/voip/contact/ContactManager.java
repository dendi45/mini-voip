package com.evanram.voip.contact;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ContactManager
{
	private List<Contact> contacts = new ArrayList<>();
	private Contact latestContact;

	public ContactManager()
	{
		//TODO load contacts from external location
		//TODO contacts may be encrypted or not
	}

	public List<Contact> getContacts()
	{
		//TODO remove this debugging stuff...
		try
		{
			for(int i = 0; i < 10; i++)
				contacts.add(new Contact(Integer.toHexString((int) (Math.random() * 100000)), InetAddress.getLocalHost(), 38936));
		}
		catch(UnknownHostException e)
		{
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

	public void addNew(Contact contact)
	{
		contacts.add(contact);
		//TODO write to disk? method shouldn't be invoked too often so disk writing would ensure that contacts cannot be created and quickly lost
	}
}
