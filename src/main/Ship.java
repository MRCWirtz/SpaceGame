package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

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

	public boolean shoot = false;
	
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
	
	public void update() {
		
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
			if (shoot) {
				Game.controller.addBullet(new Bullet(x, y, shipAngle, +1, Game.game));
				Game.controller.addBullet(new Bullet(x, y, shipAngle, -1, Game.game));
				shoot = false;
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
	
	public void draw(Graphics g, ImageObserver io) {
		
		// draw the radar
		g.setColor(new Color(255, 255, 255, 30));
		float xRadar = Game.dim.width - Game.radarSize - 20;
		float yRadar = 20;
		g.fillRect((int) xRadar, (int) yRadar, (int) Game.radarSize, (int) Game.radarSize);

		try {
			RenderPanel.shipRadar = ImageIO.read(new File(RenderPanel.imagePath + "spacecraftRadar.gif"));
			if (UserInteraction.keys[KeyEvent.VK_W] & Frame.followMode == true & Game.ship.isTurbo == true & Game.ship.getFuelLevel() > 0)
				RenderPanel.shipSprite = ImageIO.read(new File(RenderPanel.imagePath + "spacecraftTurbo.gif"));
			else if (UserInteraction.keys[KeyEvent.VK_W] & Frame.followMode == true & Game.ship.getFuelLevel() > 0)
				RenderPanel.shipSprite = ImageIO.read(new File(RenderPanel.imagePath + "spacecraftAcc.gif"));
			else
				RenderPanel.shipSprite = ImageIO.read(new File(RenderPanel.imagePath + "spacecraft.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		AffineTransform tx = AffineTransform.getRotateInstance(Game.ship.getShipAngle(), RenderPanel.shipSprite.getWidth() / 2, RenderPanel.shipSprite.getHeight() / 2);
		AffineTransform txRadar = AffineTransform.getRotateInstance(Game.ship.getShipAngle(), RenderPanel.shipRadar.getWidth() / 2, RenderPanel.shipRadar.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		AffineTransformOp opRadar = new AffineTransformOp(txRadar, AffineTransformOp.TYPE_BILINEAR);

		float xcoord = (Game.ship.getX() - Frame.xCenter) * Frame.scale + Game.dim.width / 2 - RenderPanel.shipSprite.getWidth() / 2;
		float ycoord = (Game.ship.getY() - Frame.yCenter) * Frame.scale + Game.dim.height / 2 - RenderPanel.shipSprite.getHeight() / 2;
		g.drawImage(op.filter(RenderPanel.shipSprite, null), (int) xcoord, (int) ycoord, io);
		
		g.drawImage(opRadar.filter(RenderPanel.shipRadar, null), (int) (xRadar + ((float) Game.ship.getX() / ((float) Universe.worldSize)) * Game.radarSize - RenderPanel.shipRadar.getWidth() / 2),
				(int) (yRadar + ((float) Game.ship.getY() / ((float) Universe.worldSize)) * Game.radarSize - RenderPanel.shipRadar.getHeight() / 2), io);

		g.setColor(new Color(255, 255, 51, 100));
		for (int cnt = 0; cnt < Universe.planetSystems.systemIdx.size(); cnt++) {
			Object currObject = Universe.planetSystems.getObject(Universe.planetSystems.systemIdx.get(cnt));
			float xSystem = currObject.getX();
			float ySystem = currObject.getY();
			g.fillOval((int) (xRadar + ((float) xSystem / ((float) Universe.worldSize)) * Game.radarSize - 3),
					(int) (yRadar + ((float) ySystem / ((float) Universe.worldSize)) * Game.radarSize - 3), 6, 6);
		}
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
