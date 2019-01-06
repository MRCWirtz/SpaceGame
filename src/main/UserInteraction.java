package main;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

public class UserInteraction {
	
	public static boolean[] keys = new boolean[222];

	public static void interactiveMode() {
		if (Game.flightMode == false) {
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
		if (ke.getExtendedKeyCode() == KeyEvent.VK_ESCAPE)
			Game.flightMode = !Game.flightMode;
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
	
	public static void mouseWheelMoved(MouseWheelEvent mwe) {
	    if (mwe.isControlDown()) {
	        if (mwe.getWheelRotation() < 0)
	        	Game.scale *= 1.25;
	        else
	        	Game.scale /= 1.25;
	    }
	}
}
