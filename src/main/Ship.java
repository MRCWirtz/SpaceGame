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
		
		if (Game.followMode == true) {
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

	public void scale() {

		float rc = 200;
		float rmax = 10;

		for (int j = 0; j < Universe.planetSystems.n; j++) {

			float xj = Universe.planetSystems.getObject(j).getX();
			float yj = Universe.planetSystems.getObject(j).getY();

			float diffxj = xj - getX();
			float diffyj = yj - getY();
			float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);

			if (disj < rmax) {
				Game.scale = 2;
				break;
			}
			if (disj < rc) {
				if (checkVel(j)) {
					Game.scale = 2 - 1 * (disj - rmax) / 200;
					rc = disj;
				}
			}
		}
		//System.out.println(game.scale);
	}

	public boolean checkVel(int j) {
		
		Object currObject = Universe.planetSystems.getObject(j);
		float xj = currObject.getX();
		float yj = currObject.getY();
		float vxj = currObject.getVx();
		float vyj = currObject.getVy();
		
		float diffxj = xj - x;
		float diffyj = yj - y;
		float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
		
		float diffvxj = vxj - vx;
		float diffvyj = vyj - vy;
		float disvj = (float) Math.sqrt(diffvxj * diffvxj + diffvyj * diffvyj);
		
		float m = currObject.getMass();
		
		if (disvj < 1.5 * Math.sqrt(2 * Physics.G * m / disj))
			return true;
		else
			return false;
		
	}
}
