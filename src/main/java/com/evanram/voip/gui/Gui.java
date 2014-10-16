package com.evanram.voip.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Timer;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import com.evanram.voip.VoIPApplication;

public class Gui implements ActionListener
{
	private VoIPApplication voip = VoIPApplication.instance;
	
	private JFrame frame;
	private Point dockingPoint;
	
	private JMenuBar menuBar;
	private JMenu menu_Call;
	private JMenuItem menuItem_NewCall;
	private JMenuItem menuItem_EndCall;
	private JMenu menu_Contacts;
	private JMenuItem menuItem_ViewContacts;
	private JMenuItem menuItem_AddContact;
	private JMenu menu_Settings;
	private JMenu menu_Protocol;
	
	private JRadioButtonMenuItem radioButtonMenuItem_UDP;
	private JRadioButtonMenuItem radioButtonMenuItem_TCP;
	
	private JRadioButtonMenuItem radioButtonAudioBuffer_24kb;
	private JRadioButtonMenuItem radioButtonAudioBuffer_18kb;
	private JRadioButtonMenuItem radioButtonAudioBuffer_16kb;
	private JRadioButtonMenuItem radioButtonAudioBuffer_10kb;
	private JRadioButtonMenuItem radioButtonAudioBuffer_8kb;
	
	private JMenu menu_App;
	private JMenuItem menuItem_Dock;
	private JMenuItem menuItem_Exit;
	
	private JPanel panel;
	private JLabel label;
	private final String label_defaultText = "Mini VoIP";
	
	private final ButtonGroup networkProtocolButtonGroup = new ButtonGroup();
	private final ButtonGroup audioBufferButtonGroup = new ButtonGroup();

	public Gui()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					initialize();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	private void initialize()
	{
		dockingPoint = new Point(5, 5);

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(dockingPoint);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		menuBar = new JMenuBar();
		menuBar.setBackground(new Color(0xF5F5F5));
		menuBar.setFont(new Font("SansSerif", Font.PLAIN, 14));
		menuBar.setBorderPainted(false);
		
		menu_Call = new JMenu("Call");
		menu_Call.setFont(new Font("SansSerif", Font.TYPE1_FONT, 14));
		menuBar.add(menu_Call);
		
		menuItem_NewCall = new JMenuItem("New Call");
		menu_Call.add(menuItem_NewCall);
		
		menuItem_EndCall = new JMenuItem("End Call");
		menu_Call.add(menuItem_EndCall);
		
		menu_Contacts = new JMenu("Contacts");
		menu_Contacts.setFont(new Font("SansSerif", Font.PLAIN, 14));
		menuBar.add(menu_Contacts);
		
		menuItem_ViewContacts = new JMenuItem("View Contacts");
		menu_Contacts.add(menuItem_ViewContacts);
		
		menuItem_AddContact = new JMenuItem("Add Contact");
		menu_Contacts.add(menuItem_AddContact);
		
		menu_Settings = new JMenu("Settings");
		menu_Settings.setFont(new Font("SansSerif", Font.PLAIN, 14));
		menuBar.add(menu_Settings);
		
		menu_Protocol = new JMenu("Protocol");
		menu_Settings.add(menu_Protocol);
		
		radioButtonMenuItem_UDP = new JRadioButtonMenuItem("UDP");
		networkProtocolButtonGroup.add(radioButtonMenuItem_UDP);
		menu_Protocol.add(radioButtonMenuItem_UDP);
		
		radioButtonMenuItem_TCP = new JRadioButtonMenuItem("TCP");
		networkProtocolButtonGroup.add(radioButtonMenuItem_TCP);
		menu_Protocol.add(radioButtonMenuItem_TCP);
		
		radioButtonMenuItem_UDP.setSelected(true);
		
		JMenu menuAudioBuffer = new JMenu("Audio Buffer");
		menu_Settings.add(menuAudioBuffer);
		
		radioButtonAudioBuffer_24kb = new JRadioButtonMenuItem("24kb");
		audioBufferButtonGroup.add(radioButtonAudioBuffer_24kb);
		menuAudioBuffer.add(radioButtonAudioBuffer_24kb);
		
		radioButtonAudioBuffer_18kb = new JRadioButtonMenuItem("18kb");
		audioBufferButtonGroup.add(radioButtonAudioBuffer_18kb);
		menuAudioBuffer.add(radioButtonAudioBuffer_18kb);
		
		radioButtonAudioBuffer_16kb = new JRadioButtonMenuItem("16kb");
		audioBufferButtonGroup.add(radioButtonAudioBuffer_16kb);
		menuAudioBuffer.add(radioButtonAudioBuffer_16kb);
		
		radioButtonAudioBuffer_10kb = new JRadioButtonMenuItem("10kb");
		audioBufferButtonGroup.add(radioButtonAudioBuffer_10kb);
		menuAudioBuffer.add(radioButtonAudioBuffer_10kb);
		
		radioButtonAudioBuffer_8kb = new JRadioButtonMenuItem("8kb");
		audioBufferButtonGroup.add(radioButtonAudioBuffer_8kb);
		menuAudioBuffer.add(radioButtonAudioBuffer_8kb);
		
		radioButtonAudioBuffer_16kb.setSelected(true);
		
		menu_App = new JMenu("App");
		menu_App.setFont(new Font("SansSerif", Font.PLAIN, 14));
		menuBar.add(menu_App);
		
		menuItem_Dock = new JMenuItem("Dock");
		menu_App.add(menuItem_Dock);
		
		menuItem_Exit = new JMenuItem("Exit");
		menu_App.add(menuItem_Exit);
		
		panel = new JPanel();
		panel.setBackground(new Color(0xFAFAFA));
		
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		label = new JLabel(label_defaultText);
		label.setFont(new Font("SansSerif", Font.TYPE1_FONT, 16));
		Timer timer_updateText = new Timer();
		timer_updateText.scheduleAtFixedRate(new UpdateLabelTask(label, label_defaultText), 1000L, 1000L);
		panel.add(label, BorderLayout.NORTH);
		
		MouseAdapter customMouseAdaptor = new ComponentDragAdapter(frame);
		panel.addMouseListener(customMouseAdaptor);
		panel.addMouseMotionListener(customMouseAdaptor);
		
		frame.setAlwaysOnTop(true);
		frame.setUndecorated(true);
		frame.setJMenuBar(menuBar);
		
		addActionListeners();
		frame.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		Object o = e.getSource();
		
		if(command.endsWith("kb"))	//audio buffer radio button
		{
			//These are hard coded, so it should never fail to parse. Not likely that 'kb' will get i18n support
			int bufferSize = Integer.parseInt(command.substring(0, command.indexOf('k')));
			VoIPApplication.bufferSize = bufferSize;
		}
		else if(o == menuItem_NewCall)
		{
			voip.start();
		}
		else if(o == menuItem_EndCall)
		{
			label.setText(label_defaultText);
			voip.endCall();
		}
		else if(o == menuItem_ViewContacts)
		{
			System.err.println("NOT IMPLEMENTED"); //TODO ActionEvent: menuItem_ViewContacts
		}
		else if(o == menuItem_AddContact)
		{
			System.err.println("NOT IMPLEMENTED"); //TODO ActionEvent: menuItem_AddContact
		}
		else if(o == radioButtonMenuItem_UDP)
		{
			System.err.println("NOT IMPLEMENTED"); //TODO ActionEvent: radioButtonMenuItem_UDP
		}
		else if(o == radioButtonMenuItem_TCP)
		{
			System.err.println("NOT IMPLEMENTED"); //TODO ActionEvent: radioButtonMenuItem_TCP
		}
		else if(o == menuItem_Dock)
		{
			frame.setLocation(dockingPoint);
		}
		else if(o == menuItem_Exit)
		{
			voip.shutdown();
		}
	}
	
	private void addActionListeners()
	{
		menuItem_NewCall.addActionListener(this);
		menuItem_EndCall.addActionListener(this);
		
		menuItem_ViewContacts.addActionListener(this);
		menuItem_AddContact.addActionListener(this);
		
		radioButtonMenuItem_UDP.addActionListener(this);
		radioButtonMenuItem_TCP.addActionListener(this);
		
		radioButtonAudioBuffer_24kb.addActionListener(this);
		radioButtonAudioBuffer_18kb.addActionListener(this);
		radioButtonAudioBuffer_16kb.addActionListener(this);
		radioButtonAudioBuffer_10kb.addActionListener(this);
		radioButtonAudioBuffer_8kb.addActionListener(this);
		
		menuItem_Dock.addActionListener(this);
		menuItem_Exit.addActionListener(this);
	}
}
