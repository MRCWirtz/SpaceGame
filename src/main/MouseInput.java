package main;

import java.awt.event.MouseEvent;

public class MouseInput {

	public static void mouseClicked(MouseEvent e) {	}

	public static void mouseEntered(MouseEvent e) { }

	public static void mouseExited(MouseEvent e) {	}

	public static void mousePressed(MouseEvent e) {

		float mx = e.getX();
		float my = e.getY();
		float[] pixelShip = Calculation.coord2pixel(Game.ship.getX(), Game.ship.getY(), Game.scale);
		if (Calculation.getDistance(mx, my, pixelShip[0], pixelShip[1]) < 0.9 * Game.ship.getSize() / 2) {
			Game.followMode = true;
			Game.followObject = -1;
		}
		else {
			for (int cnt = 0; cnt < Universe.planetSystems.n; cnt++) {
				Object currObject = Universe.planetSystems.getObject(cnt);
				float[] pixelObject = Calculation.coord2pixel(currObject.getX(), currObject.getY(), Game.scale);
				if (Calculation.getDistance(mx, my, pixelObject[0], pixelObject[1]) < 0.9 * currObject.getR() * Game.scale) {
					Game.followMode = true;
					Game.followObject = cnt;
				}
			}
		}
		
	}

	public static void mouseReleased(MouseEvent e) { }

}
