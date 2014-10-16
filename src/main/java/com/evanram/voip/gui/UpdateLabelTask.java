package com.evanram.voip.gui;

import java.util.TimerTask;

import javax.swing.JLabel;

import com.evanram.voip.VoIPApplication;

public class UpdateLabelTask extends TimerTask
{
	private JLabel label;
	private String defaultString;
	private VoIPApplication voip = VoIPApplication.instance;
	
	public UpdateLabelTask(JLabel label, String defaultString)
	{
		this.label = label;
		this.defaultString = defaultString;
	}

	@Override
	public void run()
	{
		String s;
		
		if(voip.isInCall())
			s = voip.getCallInfo();
		else
			s = defaultString;
		
		label.setText(s);
	}
}
