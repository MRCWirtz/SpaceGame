package main;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Universe {
	// static Game game = Game.game;

	// public Random random;

	static float worldSize = 10000;
	// set the universe
	static int nStars = 5000;
	static int nSun = 1;
	static int nPlanets = 9;

	static float rPlanetMin = 10;
	static float rPlanetMax = 50;
	static float rPlanetScale = 500;

	static float rsunMin = 100;
	static float rsunMax = 200;

	static float trackCut = (float) 0.05;	// only track high ratios of m/R^2

	// set the background stars
	static ArrayList<Point> stars = new ArrayList<Point>();
	static ArrayList<Integer> starBrightness = new ArrayList<Integer>();

	// All object positions / velocities and accelerations for gravitational interacting objects
	static public ArrayList<Point2D.Float> objects = new ArrayList<Point2D.Float>();
	static public ArrayList<Point2D.Float> objectVelocity = new ArrayList<Point2D.Float>();
	static public ArrayList<Point2D.Float> objectAcceleration = new ArrayList<Point2D.Float>();
	// stores if an object is still existing (true / false)
	static public ArrayList<Boolean> objectState = new ArrayList<Boolean>();
	// radius, mass and label of the object
	static public ArrayList<Float> rObj = new ArrayList<Float>();
	static public ArrayList<Float> mObj = new ArrayList<Float>();
	static public ArrayList<String> label = new ArrayList<String>();

	static public ArrayList<PlanetSystem> planetSystems = new ArrayList();

	static Random random =  new Random();

	public static void initGame(Game game) {
			// Place the background stars
			Random random = new Random();
			for (int i = 0; i < nStars; i++) {
				starBrightness.add(random.nextInt(200));
				stars.add(new Point(random.nextInt(game.dim.width), random.nextInt(game.dim.height)));

			}

			float rSun = rsunMin + (rsunMax - rsunMin) * random.nextFloat();
			PlanetSystem planetSystem = new PlanetSystem(worldSize / 2, worldSize / 2, rSun);
			for (int i = 0; i < nPlanets; i++) {
				float r = (rPlanetMax - rPlanetMin) * random.nextFloat() + rPlanetMin;
				planetSystem.addPlanet(300 + (float) Math.pow(i, 2) * 50, r, 1000, 5);
			}
			planetSystems.add(planetSystem);

			// Initializing the ship start parameters
				Ship ship = new Ship();

			// Initializing the interaction matrices
			for (int i = 0; i < objects.size(); i++) {

				ArrayList<Integer> indices = new ArrayList<Integer>();
				float xi = objects.get(i).x;
				float yi = objects.get(i).y;

				for (int j = 0; j < objects.size(); j++) {

					if (j == i)
						continue;

					float xj = objects.get(j).x;
					float yj = objects.get(j).y;

					float diffxj = xj - xi;
					float diffyj = yj - yi;
					float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
					if (mObj.get(j) / Math.pow(disj, 2) > trackCut || disj <= 2 * (rObj.get(i) + rObj.get(j)))
						indices.add(j);
				}
				trajObj.add(indices);

				float diffxShip = ship.getX() - xi;
				float diffyShip = ship.getY() - yi;
				float disShip = (float) Math.sqrt(diffxShip * diffxShip + diffyShip * diffyShip);
				if (mObj.get(i) / Math.pow(disShip, 2) > 0.5 * trackCut || disShip <= 2 * rObj.get(i))
					ship.trajShip.add(i);
			}
		}
}
