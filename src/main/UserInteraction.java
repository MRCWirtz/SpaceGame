package main;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;

public class UserInteraction {
	
	public static boolean[] keys = new boolean[222];

	public static void interactiveMode() {
		if (Game.followMode == false) {
			if (keys[KeyEvent.VK_W])
				Game.yCenter -= 5 / Game.scale;
			if (keys[KeyEvent.VK_S])
				Game.yCenter += 5 / Game.scale;
			if (keys[KeyEvent.VK_A])
				Game.xCenter -= 5 / Game.scale;
			if (keys[KeyEvent.VK_D])
				Game.xCenter += 5 / Game.scale;
		}
	}
	
	public static void keyPressed(KeyEvent ke) {
		if (ke.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
			Game.followMode = !Game.followMode;
			if (Game.followMode == true)
				Game.followObject = -1;		// default follow the ship
		}
		if (ke.getExtendedKeyCode() == KeyEvent.VK_PLUS || ke.getExtendedKeyCode() == KeyEvent.VK_PAGE_UP)
			Game.scale *= 1.25;
		if (ke.getExtendedKeyCode() == KeyEvent.VK_MINUS || ke.getExtendedKeyCode() == KeyEvent.VK_PAGE_DOWN)
			Game.scale /= 1.25;
		keys[ke.getKeyCode()] = true;
	}
	
	public static void keyReleased(KeyEvent ke) {
		keys[ke.getKeyCode()] = false;
	}
	
	public static void keyTyped(KeyEvent ke) { }
	
	public static void mousePressed(MouseEvent me) {
		
	}
	
	public static void mouseWheelMoved(MouseWheelEvent mwe) {
        if (mwe.getWheelRotation() < 0)
        	Game.scale *= 1.125;
        else
        	Game.scale /= 1.125;
	}
}
