package main;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Ship {
	
	static Setup set = Setup.setup;
	
	public static void move() {
		
		if (set.tick % 100 == 0)
			updateTrack();

		float x = set.ship.x;
		float y = set.ship.y;
		float vx = set.shipVelocity.x;
		float vy = set.shipVelocity.y;
		float acc = set.shipAcceleration;
		
		if (set.keys[KeyEvent.VK_SHIFT])
			acc *= 10;
		if (set.keys[KeyEvent.VK_A])
			set.shipAngle -= set.rotSpeed;
		if (set.keys[KeyEvent.VK_D])
			set.shipAngle += set.rotSpeed;
		if (set.keys[KeyEvent.VK_W]) {
			vx += acc * Math.sin(set.shipAngle);
			vy -= acc * Math.cos(set.shipAngle);
		}
		
		for (int j: set.trajShip) {
			
			if (set.objectState.get(j) == false)
					continue;
			
			float xj = set.objects.get(j).x;
			float yj = set.objects.get(j).y;
	
			float diffxj = xj - x;
			float diffyj = yj - y;
			float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
			float mj = set.mObj.get(j);
			float rj = set.rObj.get(j);
			
			if (disj < rj) {
				set.over = true;
			}
			
			vx += set.G * mj * diffxj / Math.pow(disj, 3);
			vy += set.G * mj * diffyj / Math.pow(disj, 3);
		}
		
		x += vx;
		y += vy;
		
		set.ship.x = x;
		set.ship.y = y;
		set.shipVelocity.x = vx;
		set.shipVelocity.y = vy;
	}
	
	public static void updateTrack() {
		
		set.trajShip = new ArrayList<Integer>();
		for (int i = 0; i < set.objects.size(); i++) {
			
			if (set.objectState.get(i) == false)
				continue;
			
			float xi = set.objects.get(i).x;
			float yi = set.objects.get(i).y;
			
			float diffxShip = set.ship.x - xi;
			float diffyShip = set.ship.y - yi;
			float disShip = (float) Math.sqrt(diffxShip * diffxShip + diffyShip * diffyShip);
			if (set.mObj.get(i) / Math.pow(disShip, 2) > 0.5 * set.trackCut || disShip <= 2 * set.rObj.get(i))
				set.trajShip.add(i);
		}
	}
	
	public static void scale() {

		float x = set.ship.x;
		float y = set.ship.y;
		
		float rc = 200;
		float rmax = 10;
		set.scale = 1;
		
		for (int j = 0; j < set.objects.size(); j++) {
			
			float xj = set.objects.get(j).x;
			float yj = set.objects.get(j).y;
	
			float diffxj = xj - x;
			float diffyj = yj - y;
			float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
			
			if (disj < rmax) {
				set.scale = 2;
				break;
			}
			if (disj < rc) {
				if (checkVel(j)) {
					set.scale = 2 - 1 * (disj - rmax) / 200;
					rc = disj;
				}
			}
		}
		//System.out.println(set.scale);
	}
	
	public static boolean checkVel(int j) {

		float x = set.ship.x;
		float y = set.ship.y;
		float vx = set.shipVelocity.x;
		float vy = set.shipVelocity.y;

		float xj = set.objects.get(j).x;
		float yj = set.objects.get(j).y;
		float vxj = set.objectVelocity.get(j).x;
		float vyj = set.objectVelocity.get(j).y;
		
		float diffxj = xj - x;
		float diffyj = yj - y;
		float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
		
		float diffvxj = vxj - vx;
		float diffvyj = vyj - vy;
		float disvj = (float) Math.sqrt(diffvxj * diffvxj + diffvyj * diffvyj);
		
		float m = set.mObj.get(j);
		
		if (disvj < 1.5 * Math.sqrt(2 * set.G * m / disj))
			return true;
		else
			return false;
		
	}

}
