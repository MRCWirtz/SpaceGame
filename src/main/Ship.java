package main;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class Ship {
	
	Game game = Game.game;
	public ArrayList<Integer> trajShip = new ArrayList<Integer>();
	// parameters
	private float shipAngle;
	private float rotSpeed;


	
	static Random random =  new Random();
	
	private float x;
	private float y;
	private float vx;
	private float vy;
	private float acc;
	public float accDef;
	public float accTurbo;
	public float G = (float) ((float) 3 * Math.pow(10, -4));
	
	public Ship() {
		x = Universe.worldSize * random.nextFloat(); 
		y = Universe.worldSize * random.nextFloat();
		vx = 0;
		vy = 0;
		accDef = (float) 0.001;
		accTurbo = (float) 0.01;
		shipAngle = 0;
		rotSpeed = (float) 0.03;
		
	}
	public float getX() {return x;}
	public float getY() {return y;}
	public float getVx() {return vx;}
	public float getVy() {return vy;}
	public double getVabs() {return  Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));}
	public float getShipAngle() {return shipAngle;}
	public float getAcc() {return acc;}
	
	public void move(Game game) {
		System.out.println(getX());
		System.out.println(game.G);
		
		if (game.tick % 100 == 0)
			updateTrack();
		
		if (game.flightMode == true) {
			if (game.keys[KeyEvent.VK_SHIFT])
				acc = accTurbo;
			else 
				acc = accDef;
			if (game.keys[KeyEvent.VK_A])
				shipAngle -= rotSpeed;
			if (game.keys[KeyEvent.VK_D])
				shipAngle += rotSpeed;
			if (game.keys[KeyEvent.VK_W]) {
				vx += acc * Math.sin(shipAngle);
				vy -= acc * Math.cos(shipAngle);
			}
		}
		
		for (int j: trajShip) {
			
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
		
	}
	
	public void updateTrack() {
		
		trajShip = new ArrayList<Integer>();
		for (int i = 0; i < Universe.objects.size(); i++) {
			
			if (Universe.objectState.get(i) == false)
				continue;
			
			float xi = Universe.objects.get(i).x;
			float yi = Universe.objects.get(i).y;
			
			float diffxShip = x - xi;
			float diffyShip = y - yi;
			float disShip = (float) Math.sqrt(diffxShip * diffxShip + diffyShip * diffyShip);
			if (Universe.mObj.get(i) / Math.pow(disShip, 2) > 0.5 * Universe.trackCut || disShip <= 2 * Universe.rObj.get(i))
				trajShip.add(i);
		}
	}
	

	
	public boolean checkVel(int j) {

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
		
		if (disvj < 1.5 * Math.sqrt(2 * G * m / disj))
			return true;
		else
			return false;
		
	}

}
