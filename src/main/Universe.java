package main;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Universe {
	// static Game game = Game.game;

	// public Random random;

	static float worldSize = 10000;
	// set the universe
	static int nStars = 5000;
	static int nSystems = 2;
	static int nPlanets = 6;

	static float rPlanetMin = 10;
	static float rPlanetMax = 50;
	static float rPlanetScale = 500;

	static float rsunMin = 150;
	static float rsunMax = 250;

	static float trackCut = (float) 0.05;	// only track high ratios of m/R^2

	// set the background stars
	static ArrayList<Point> stars = new ArrayList<Point>();
	static ArrayList<Integer> starBrightness = new ArrayList<Integer>();

	static public PlanetSystems planetSystems = new PlanetSystems();

	static Random random =  new Random();

	public static void initGame() {
			// Place the background stars
			Random random = new Random();
			for (int i = 0; i < nStars; i++) {
				starBrightness.add(random.nextInt(200));
				stars.add(new Point(random.nextInt(Game.dim.width), random.nextInt(Game.dim.height)));

			}
			for (int j = 0; j < nSystems; j++) {
				float rSun = rsunMin + (rsunMax - rsunMin) * random.nextFloat();
				planetSystems.addStar((float) (j + 1) * worldSize / 3, (float) (j + 1) * worldSize / 3, rSun);
				for (int i = 0; i < nPlanets; i++) {
					float r = (rPlanetMax - rPlanetMin) * random.nextFloat() + rPlanetMin;
					float exponent = (float) 1.8 + (float) 0.4 * random.nextFloat();
					planetSystems.addPlanet(j, 400 + (float) Math.pow(i, exponent) * 100, r, 1000, 5);
				}
			}
	}
}
