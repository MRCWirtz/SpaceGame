package main;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;

public class UserInteraction {
	
	public static boolean[] keys = new boolean[222];

	public static void interactiveMode() {
		if (Frame.followMode == false) {
			if (keys[KeyEvent.VK_W])
				Frame.yCenter -= 5 / Frame.scale;
			if (keys[KeyEvent.VK_S])
				Frame.yCenter += 5 / Frame.scale;
			if (keys[KeyEvent.VK_A])
				Frame.xCenter -= 5 / Frame.scale;
			if (keys[KeyEvent.VK_D])
				Frame.xCenter += 5 / Frame.scale;
		}
	}
	
	public static void keyPressed(KeyEvent ke) {
		if (ke.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
			Frame.followMode = !Frame.followMode;
			if (Frame.followMode == true)
				Frame.followObject = -1;		// default follow the ship
		}
		if (ke.getExtendedKeyCode() == KeyEvent.VK_PLUS || ke.getExtendedKeyCode() == KeyEvent.VK_PAGE_UP) {
			Frame.scale *= 1.25;
			Frame.autoScaleDead = 500;
		}
		if (ke.getExtendedKeyCode() == KeyEvent.VK_MINUS || ke.getExtendedKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			Frame.scale /= 1.25;
			Frame.autoScaleDead = 500;
		}
		Frame.scale = Math.min(Math.max(Frame.scale, (float) 0.1), 10); 
		if (ke.getExtendedKeyCode() == KeyEvent.VK_SPACE)
			Game.ship.shoot = true;

		keys[ke.getKeyCode()] = true;
	}
	
	public static void keyReleased(KeyEvent ke) {
		keys[ke.getKeyCode()] = false;
	}
	
	public static void keyTyped(KeyEvent ke) { }
	
	public static void mousePressed(MouseEvent me) {
		
	}
	
	public static void mouseWheelMoved(MouseWheelEvent mwe) {
		Frame.autoScaleDead = 500;
        if (mwe.getWheelRotation() < 0)
        	Frame.scale *= 1.1;
        else
        	Frame.scale /= 1.1;
        Frame.scale = Math.min(Math.max(Frame.scale, (float) 0.1), 10); 
	}
}
