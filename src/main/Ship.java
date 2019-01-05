package main;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Ship {
	
	static Game game = Game.game;
	
	public static void move() {
		
		if (game.tick % 100 == 0)
			updateTrack();

		float x = game.ship.x;
		float y = game.ship.y;
		float vx = game.shipVelocity.x;
		float vy = game.shipVelocity.y;
		float acc = game.shipAcceleration;
		
		if (game.shipMode == true) {
			if (game.keys[KeyEvent.VK_SHIFT])
				acc *= 10;
			if (game.keys[KeyEvent.VK_A])
				game.shipAngle -= game.rotSpeed;
			if (game.keys[KeyEvent.VK_D])
				game.shipAngle += game.rotSpeed;
			if (game.keys[KeyEvent.VK_W]) {
				vx += acc * Math.sin(game.shipAngle);
				vy -= acc * Math.cos(game.shipAngle);
			}
		}
		
		for (int j: game.trajShip) {
			
			if (Universe.objectState.get(j) == false)
					continue;
			
			float xj = Universe.objects.get(j).x;
			float yj = Universe.objects.get(j).y;
	
			float diffxj = xj - x;
			float diffyj = yj - y;
			float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
			float mj = Universe.mObj.get(j);
			float rj = Universe.rObj.get(j);
			
			if (disj < rj) {
				game.over = true;
			}
			
			vx += game.G * mj * diffxj / Math.pow(disj, 3);
			vy += game.G * mj * diffyj / Math.pow(disj, 3);
		}
		
		x += vx;
		y += vy;
		
		game.ship.x = x;
		game.ship.y = y;
		game.shipVelocity.x = vx;
		game.shipVelocity.y = vy;
	}
	
	public static void updateTrack() {
		
		game.trajShip = new ArrayList<Integer>();
		for (int i = 0; i < Universe.objects.size(); i++) {
			
			if (Universe.objectState.get(i) == false)
				continue;
			
			float xi = Universe.objects.get(i).x;
			float yi = Universe.objects.get(i).y;
			
			float diffxShip = game.ship.x - xi;
			float diffyShip = game.ship.y - yi;
			float disShip = (float) Math.sqrt(diffxShip * diffxShip + diffyShip * diffyShip);
			if (Universe.mObj.get(i) / Math.pow(disShip, 2) > 0.5 * Universe.trackCut || disShip <= 2 * Universe.rObj.get(i))
				game.trajShip.add(i);
		}
	}
	
	public static void scale() {

		float x = game.ship.x;
		float y = game.ship.y;
		
		float rc = 200;
		float rmax = 10;
		game.scale = 1;
		
		for (int j = 0; j < Universe.objects.size(); j++) {
			
			float xj = Universe.objects.get(j).x;
			float yj = Universe.objects.get(j).y;
	
			float diffxj = xj - x;
			float diffyj = yj - y;
			float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
			
			if (disj < rmax) {
				game.scale = 2;
				break;
			}
			if (disj < rc) {
				if (checkVel(j)) {
					game.scale = 2 - 1 * (disj - rmax) / 200;
					rc = disj;
				}
			}
		}
		//System.out.println(game.scale);
	}
	
	public static boolean checkVel(int j) {

		float x = game.ship.x;
		float y = game.ship.y;
		float vx = game.shipVelocity.x;
		float vy = game.shipVelocity.y;

		float xj = Universe.objects.get(j).x;
		float yj = Universe.objects.get(j).y;
		float vxj = Universe.objectVelocity.get(j).x;
		float vyj = Universe.objectVelocity.get(j).y;
		
		float diffxj = xj - x;
		float diffyj = yj - y;
		float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
		
		float diffvxj = vxj - vx;
		float diffvyj = vyj - vy;
		float disvj = (float) Math.sqrt(diffvxj * diffvxj + diffvyj * diffvyj);
		
		float m = Universe.mObj.get(j);
		
		if (disvj < 1.5 * Math.sqrt(2 * game.G * m / disj))
			return true;
		else
			return false;
		
	}

}
