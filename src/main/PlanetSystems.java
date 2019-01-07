package main;

import java.util.ArrayList;

public class PlanetSystems {

	public int n = 0;
	public ArrayList<Integer> systemIdx = new ArrayList<Integer>();
	private ArrayList<Object> objects = new ArrayList<Object>();
	
	public PlanetSystems() {
		return;
	}
	
	public void addStar(float posX, float posY, float r) {

		float mass = (float) Math.pow(r, 3) / 3;
		Object object = new Object(r, mass, posX, posY);
		objects.add(object);
		systemIdx.add(this.n);
		this.n ++;
	}
	
	public void addPlanet(int system, float R, float r, float fuel, float fuelRate) {
		
		Object currSystem = getObject(this.systemIdx.get(system));
		float mass = (float) Math.pow(r, 3);
		Object object = new Object(r, mass, currSystem.getX(), currSystem.getY());
		object.setOrbit(R, currSystem.getMass());
		object.setFuelRecource(fuel, fuelRate);
		objects.add(object);
		this.n ++;
		return;
	}
	
	public void update() {
		for (int cnt = 0; cnt < objects.size(); cnt++)
			objects.get(cnt).update();
	}

	public Object getObject(int idx) { return objects.get(idx); }
	
	public float getN() { return n; }

}
