package main;

import java.util.Random;

public class Planet {
	
	private float posX;
	private float posY;
	private float phi;
	private float omega;

	private float r;
	private float mass;
	private float R;
	
	private float fuel;
	private float fuelRate;

	static Random random =  new Random();
	
	public Planet(float R, float r, float mSun) {

		this.phi = 2 * (float) Math.PI * random.nextFloat();
		this.r = r;
		this.R = R;
		this.omega = (float) Math.sqrt(mSun * 3 * (float) Math.pow(10, -4) / (float) Math.pow(R, 3));
	}
	
	public void move() {
		this.phi += omega;
		if (phi > 2 * (float) Math.PI ) { this.phi -= 2 * (float) Math.PI; }
		this.posX = R * (float) Math.cos(phi);
		this.posY = R * (float) Math.sin(phi);
	}
	
	public void setFuelRecource(float fuel, float fuelRate) {
		this.fuel = fuel;
		this.fuelRate = fuelRate;
	}
	
	public float getX() { return posX; }

	public float getY() { return posY; }

	public float getR() { return r; }

}
