package main;

import java.util.ArrayList;

public class PlanetSystem {

	private int n;
	
	private float posX;
	private float posY;

	private float r;
	private float mass;
	private ArrayList<Planet> planets = new ArrayList();
	
	public PlanetSystem(float posX, float posY, float r) {
		
		this.posX = posX;
		this.posY = posY;
		this.r = r;
		this.mass = (float) Math.pow(r, 3) / 3;
	}
	
	public void addPlanet(float R, float r, float fuel, float fuelRate) {

		Planet planet = new Planet(R, r, this.mass);
		planet.setFuelRecource(fuel, fuelRate);
		planets.add(planet);
		this.n ++;
		return;
	}
	
	public void move() {
		for (int cnt = 0; cnt < planets.size(); cnt++) {
			planets.get(cnt).move();
		}
	}

	public Planet getPlanet(int i) { return (Planet) planets.get(i); }

	public float getX() { return posX; }

	public float getY() { return posY; }

	public float getR() { return r; }

	public float getMass() { return mass; }
	
	public float getN() { return n; }

}
