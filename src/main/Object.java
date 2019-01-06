package main;

import java.util.Random;

public class Object {
	
	private float posX;
	private float posY;
	private float posStartX;
	private float posStartY;
	private float phi = 0;
	private float omega = 0;
	private float velX = 0;
	private float velY = 0;

	private float r;
	private float mass;
	private float R;
	
	private float fuel = 0;
	private float fuelRate = 0;
	
	public boolean isPlanet = false;

	static Random random =  new Random();
	
	public Object(float r, float mass, float posX, float posY) {
		this.r = r;
		this.mass = mass;
		this.posX = posX;
		this.posY = posY;
		this.posStartX = posX;
		this.posStartY = posY;
	}

	public void setOrbit(float R, float mSun) {
		this.isPlanet = true;
		this.phi = 2 * (float) Math.PI * random.nextFloat();
		this.omega = (float) Math.sqrt(mSun * 3 * (float) Math.pow(10, -4) / (float) Math.pow(R, 3));
		this.R = R;
	}
	
	public void move() {

		if (this.isPlanet == true) {
			this.phi += omega;
			if (phi > 2 * (float) Math.PI )
				this.phi -= 2 * (float) Math.PI;
			this.posX = posStartX + R * (float) Math.cos(phi);
			this.posY = posStartY + R * (float) Math.sin(phi);
			this.velX = - (float) Math.sin(phi) * omega * R;
			this.velY = - (float) Math.cos(phi) * omega * R;
		}
	}
	
	public void setFuelRecource(float fuel, float fuelRate) {
		this.fuel = fuel;
		this.fuelRate = fuelRate;
	}
	
	public float getX() { return posX; }

	public float getY() { return posY; }
	
	public float getXFuture(int timesteps) {
		if (this.isPlanet == false)
			return posX;
		else
			return (posStartX + R * (float) Math.cos(phi + (float) timesteps * omega));
		}

	public float getYFuture(int timesteps) {
		if (this.isPlanet == false)
			return posY;
		else
			return (posStartY + R * (float) Math.sin(phi + (float) timesteps * omega));
		}
	
	public float getVx() { return velX; }

	public float getVy() { return velY; }

	public float getR() { return r; }
	
	public float getMass() { return mass; }

}
