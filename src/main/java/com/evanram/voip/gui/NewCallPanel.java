package com.evanram.voip.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.evanram.voip.VoIPApplication;
import com.evanram.voip.contact.Contact;

public class NewCallPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 2938641144058078485L;

	private Gui parent;
	private JComboBox<String> comboBox;
	private JButton button_Call;

	public NewCallPanel(Gui parent)
	{
		this.parent = parent;

		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					initialize();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	private boolean colorFlag = false;

	//TODO this code is really bad, probably want to fix it up...
	private void initialize()
	{
		setOpaque(false);
		comboBox = new JComboBox<>();
		addContactsToComboBox();

		add(comboBox, BorderLayout.NORTH);

		final String buttonTitle = "Call";
		button_Call = new JButton(buttonTitle)
		{
			private static final long serialVersionUID = 7950272947459436837L;

			@Override
			protected void paintComponent(Graphics g)
			{
				Color color;

				if(colorFlag = !colorFlag)
					color = new Color(0xDBFDFF);
				else
					color = new Color(0xEBFEFF);

				super.paintComponent(g);
				g.setColor(color);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(Color.BLACK);
				g.setFont(new Font("SansSerif", Font.PLAIN, 16));
				FontMetrics fontMetrics = g.getFontMetrics();
				Gui.staticCacheFontMetrics = fontMetrics;
				int stringLength = fontMetrics.stringWidth(buttonTitle);
				int heightDif = 5; //offset of string on y axis
				g.drawString(buttonTitle, getWidth() / 2 - stringLength / 2, getHeight() / 2 + heightDif);
			}
		};

		button_Call.addActionListener(this);
		button_Call.setBorderPainted(false);
		add(button_Call, BorderLayout.CENTER);
	}

	private void addContactsToComboBox()
	{
		List<Contact> contacts = VoIPApplication.contactManager.getContacts();

		for(Contact contact : contacts)
			comboBox.addItem(contact.getName());
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource().equals(button_Call))
		{
			parent.removeNewCallPanel();
			Contact contact = VoIPApplication.contactManager.get((String) comboBox.getSelectedItem());
			VoIPApplication.instance.start(contact);
		}
	}
}
