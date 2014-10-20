package com.evanram.voip.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import com.evanram.voip.Utils;
import com.evanram.voip.VoIPApplication;

public class Gui implements ActionListener
{
	public static FontMetrics staticCacheFontMetrics;
	private static JFrame frame;
	
	private VoIPApplication voip = VoIPApplication.instance;
	
	private Point[] dockingPoints;
	
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
	
	private JMenu menu_App;
	private JMenuItem menuItem_Dock;
	private JMenuItem menuItem_Exit;
	
	private JPanel panel_Main;
	private JLabel mainLabel;
	private final String label_defaultText = "Mini VoIP";
	
	private final ButtonGroup networkProtocolButtonGroup = new ButtonGroup();
	private JPanel panel_newCallWrappingPanel;
	private Component horizontalGlue;
	private Component horizontalGlue_1;
	
	private Color color_almostWhite = new Color(0xFAFAFA);

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
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent arg0)
			{
				frame.setExtendedState(JFrame.ICONIFIED);
			}
		});
		
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
		
		menu_App = new JMenu("App");
		menu_App.setFont(new Font("SansSerif", Font.PLAIN, 14));
		menuBar.add(menu_App);
		
		menuItem_Dock = new JMenuItem("Dock");
		menu_App.add(menuItem_Dock);
		
		menuItem_Exit = new JMenuItem("Exit");
		menu_App.add(menuItem_Exit);
		
		panel_newCallWrappingPanel = new JPanel();
		panel_newCallWrappingPanel.setBackground(color_almostWhite);
		
		//frame.getContentPane().add(panel_newCallWrappingPanel);
		panel_Main = new JPanel();
		panel_Main.setBackground(color_almostWhite);
		
		frame.add(panel_Main, BorderLayout.SOUTH);
		mainLabel = new JLabel(label_defaultText);
		mainLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		panel_Main.setLayout(new BoxLayout(panel_Main, BoxLayout.X_AXIS));
		
		horizontalGlue_1 = Box.createHorizontalGlue();
		panel_Main.add(horizontalGlue_1);
		panel_Main.add(mainLabel);
		
		horizontalGlue = Box.createHorizontalGlue();
		panel_Main.add(horizontalGlue);
		
		MouseAdapter customMouseAdaptor = new ComponentDragAdapter(frame);
		panel_Main.addMouseListener(customMouseAdaptor);
		panel_Main.addMouseMotionListener(customMouseAdaptor);
		panel_newCallWrappingPanel.addMouseListener(customMouseAdaptor);
		panel_newCallWrappingPanel.addMouseMotionListener(customMouseAdaptor);
		
		frame.setAlwaysOnTop(true);
		frame.setUndecorated(true);
		frame.setJMenuBar(menuBar);
		
		addActionListeners();
		frame.pack();
		
		setupDockingPoints();
		frame.setLocation(dockingPoints[0]);
		
		new Timer().scheduleAtFixedRate(new UpdateTextTask(frame, mainLabel, label_defaultText), 0L, 250L);	//updates call info 'contact_name - 0:00:00'
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object o = event.getSource();
		
		if(o == menuItem_NewCall)
		{
			startNewCallPanel();
		}
		else if(o == menuItem_EndCall)
		{
			mainLabel.setText(label_defaultText);
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
			frame.setLocation(Utils.getNearestPoint(frame.getLocation(), dockingPoints));
		}
		else if(o == menuItem_Exit)
		{
			voip.shutdown();
		}
	}
	
	public int getWidth()
	{
		return frame.getWidth();
	}
	
	private void addActionListeners()
	{
		menuItem_NewCall.addActionListener(this);
		menuItem_EndCall.addActionListener(this);
		
		menuItem_ViewContacts.addActionListener(this);
		menuItem_AddContact.addActionListener(this);
		
		radioButtonMenuItem_UDP.addActionListener(this);
		radioButtonMenuItem_TCP.addActionListener(this);
		
		menuItem_Dock.addActionListener(this);
		menuItem_Exit.addActionListener(this);
	}
	
	public void removeNewCallPanel()
	{
		panel_newCallWrappingPanel.removeAll();
		frame.remove(panel_newCallWrappingPanel);
		frame.pack();
	}
	
	private void startNewCallPanel()
	{
		frame.add(panel_newCallWrappingPanel, BorderLayout.NORTH);
		panel_newCallWrappingPanel.add(new NewCallPanel(this));
		panel_newCallWrappingPanel.add(Box.createVerticalStrut(40));
		frame.pack();
	}
	
	private void setupDockingPoints()
	{
		final int OFFSET = 5;
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		int sW = screenDimension.width;
		int sH = screenDimension.height;
		int fW = frame.getWidth();
		int fH = frame.getHeight();
		
		dockingPoints = new Point[]
		{
			new Point(OFFSET, OFFSET),                                     // top-left
			new Point(sW - OFFSET - fW, OFFSET),                           // top-right
			new Point(OFFSET, sH - OFFSET - fH),                           // bottom-left
			new Point(sW - OFFSET - fW, sH - OFFSET - fH),                 // bottom-right
			new Point((sW / 2) - (fW / 2), OFFSET),                        // top-middle
			new Point((sW / 2) - (fW / 2), (sH / 2) - (fH / 2)),           // middle-middle
			new Point((sW / 2) - (fW / 2), sH - OFFSET - (fH * 2))         // near-bottom-middle (above taskbar on many machines)
		};
	}
}
