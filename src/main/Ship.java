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
	
	private float size;
	
	private float x;
	private float y;
	private float vx;
	private float vy;
	private float acc;
	private float fuelLevel;
	private float fuelConsTurbo;
	private float fuelConsDef;

	public boolean isTurbo;
	public float accDef;
	public float accTurbo;
	public float G = (float) ((float) 3 * Math.pow(10, -4));
	
	public Ship() {
		size = 40;	// pixel edge size
		x = Universe.worldSize * random.nextFloat(); 
		y = Universe.worldSize * random.nextFloat();
		vx = 0;
		vy = 0;
		accDef = (float) 0.003;
		accTurbo = (float) 0.01;
		shipAngle = 0;
		rotSpeed = (float) 0.05;
		fuelLevel = (float) 1.;
		fuelConsTurbo = (float) 3e-4;
		fuelConsDef = (float) 1e-4;
		isTurbo = false;
		
	}
	public float getX() {return x;}
	public float getY() {return y;}
	public float getVx() {return vx;}
	public float getVy() {return vy;}
	public double getVabs() {return  Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));}
	public float getShipAngle() {return shipAngle;}
	public float getAcc() {return acc;}
	public void setFuelLevel( float newFuelLevel) { Math.max(0, this.fuelLevel = newFuelLevel);}
	public float getFuelLevel() { return fuelLevel;}
	public float getSize() {return size;}
	
	public void move() {
		
		if (Frame.followMode == true) {
			if (UserInteraction.keys[KeyEvent.VK_SHIFT])
				isTurbo = true;	
			else
				isTurbo = false;
			if (UserInteraction.keys[KeyEvent.VK_A])
				shipAngle -= rotSpeed;
			if (UserInteraction.keys[KeyEvent.VK_D])
				shipAngle += rotSpeed;
			if (UserInteraction.keys[KeyEvent.VK_W] & this.fuelLevel > 0.0) {
				if (isTurbo) {
					this.setFuelLevel(this.getFuelLevel() - this.fuelConsTurbo);
					acc = accTurbo;
				}
				else {
					this.setFuelLevel(this.getFuelLevel() - this.fuelConsDef);	
					acc = accDef;
				}
				vx += acc * Math.sin(shipAngle);
				vy -= acc * Math.cos(shipAngle);
			}
		}
		
		for (int cnt = 0; cnt < Universe.planetSystems.getN(); cnt++) {
			Object currObject = Universe.planetSystems.getObject(cnt);
			float[] velocityUpdate = currObject.getGravity(x, y);

			if ( velocityUpdate[2] < currObject.getR() )
				Game.over = true;
			vx += velocityUpdate[0];
			vy += velocityUpdate[1];
		}

		x += vx;
		y += vy;
		
	}

	public boolean checkVel(int j) {
		
		// check if the velocity is sufficiently small to be bound in orbit
		Object currObject = Universe.planetSystems.getObject(j);
		float disj = distanceObject(currObject);
		float disvj = Calculation.getDistance(getVx(), getVy(), currObject.getVx(), currObject.getVy());
		float m = currObject.getMass();
		if (disvj < Math.sqrt(2 * Physics.G * m / disj))
			return true;
		else
			return false;
	}
	
	public float distanceObject(int objectId) {
		Object object = Universe.planetSystems.getObject(objectId);
		return distanceObject(object);
	}

	public float distanceObject(Object object) {
		float dis = Calculation.getDistance(getX(), getY(), object.getX(), object.getY());
		return dis;
	}
	
}
