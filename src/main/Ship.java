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
	private float fuelLevel;
	
	public Ship() {
		x = Universe.worldSize * random.nextFloat(); 
		y = Universe.worldSize * random.nextFloat();
		vx = 0;
		vy = 0;
		accDef = (float) 0.001;
		accTurbo = (float) 0.01;
		shipAngle = 0;
		rotSpeed = (float) 0.03;
		fuelLevel = (float) 0.7;
		
	}
	public float getX() {return x;}
	public float getY() {return y;}
	public float getVx() {return vx;}
	public float getVy() {return vy;}
	public double getVabs() {return  Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));}
	public float getShipAngle() {return shipAngle;}
	public float getAcc() {return acc;}
	public void setFuelLevel( float newFuelLevel) { this.fuelLevel = newFuelLevel;}
	public float getFuelLevel() { return fuelLevel;}
	
	public void move(Game game) {
		System.out.println(getX());
		System.out.println(game.G);
		
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
		
		for (int cnt = 0; cnt < Universe.planetSystems.size(); cnt++) {
			PlanetSystem currSystem = Universe.planetSystems.get(cnt);
			float xi = currSystem.getX();
			float yi = currSystem.getY();
			float diffxi = xi - x;
			float diffyi = yi - y;
			float disi = (float) Math.sqrt(diffxi * diffxi + diffyi * diffyi);
			float mi = currSystem.getMass();
			float ri = currSystem.getR();
			
			if (disi < ri)
				game.over = true;
			
			vx += game.G * mi * diffxi / Math.pow(disi, 3);
			vy += game.G * mi * diffyi / Math.pow(disi, 3);

			for (int cntPlanet = 0; cntPlanet < currSystem.getN(); cntPlanet++) {
				Planet currPlanet = currSystem.getPlanet(cntPlanet);
				float xj = xi + currPlanet.getX();
				float yj = yi + currPlanet.getY();
				float diffxj = xj - x;
				float diffyj = xj - y;
				float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
				float mj = currPlanet.getMass();
				float rj = currPlanet.getR();
				
				if (disj < rj)
					game.over = true;
				
				vx += game.G * mj * diffxj / Math.pow(disj, 3);
				vy += game.G * mj * diffyj / Math.pow(disj, 3);
			}
		}

		x += vx;
		y += vy;
		
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
