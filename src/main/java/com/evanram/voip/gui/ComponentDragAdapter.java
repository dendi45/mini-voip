package com.evanram.voip.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//referenced from http://stackoverflow.com/a/13171534
public class ComponentDragAdapter extends MouseAdapter
{
	private Component parent;
	private Point p;

	public ComponentDragAdapter(Component parent)
	{
		this.parent = parent;
	}

	@Override
	public void mouseDragged(MouseEvent event)
	{
		Point parentLocation = parent.getLocation();
		int oX = parentLocation.x;
		int oY = parentLocation.y;
		int dX = (oX + event.getX()) - (oX + p.x);
		int dY = (oY + event.getY()) - (oY + p.y);
		int x = oX + dX;
		int y = oY + dY;

		parent.setLocation(x, y);
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		p = event.getPoint();
	}
}