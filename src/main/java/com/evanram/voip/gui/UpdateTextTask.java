package com.evanram.voip.gui;

import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.evanram.voip.VoIPApplication;

public class UpdateTextTask extends TimerTask
{
	private JFrame frame;
	private JLabel label;
	private String defaultString;
	private int blinkCounter = -1;
	private VoIPApplication voip = VoIPApplication.instance;
	
	public UpdateTextTask(JFrame frame, JLabel label, String defaultString)
	{
		this.frame = frame;
		this.label = label;
		this.defaultString = defaultString;
	}

	@Override
	public void run()
	{
		String labelText, titleText;
		char blink;
		
		switch(++blinkCounter)	//displays a blinking dot next to taskbar
		{
			case 0:
				blink = '\u25DD'; //top-right
				break;
			case 1:
				blink = '\u25DE'; //bottom-right
				break;
			case 2:
				blink = '\u25DF'; //bottom-left
				break;
			default:
				blink = '\u25DC'; //top-left
				blinkCounter = -1;
		}
		
		if(voip.isInCall())
		{
			labelText = voip.getCallInfo();
			titleText = voip.getCallTime() + "    " + blink;	//info is too long to display in taskbar, so show the time
		}
		else
			labelText = titleText = defaultString;
		
		label.setText(labelText);
		frame.setTitle(titleText);
	}
}
