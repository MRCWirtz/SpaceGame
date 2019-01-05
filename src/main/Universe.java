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
	static int nBH = 1;
	static int nSun = 6;
	static int nPlanets = 50;
	
	static float rPlanetMin = 5;
	static float rPlanetMax = 20;
	static float rPlanetScale = 500;
	
	static float rsunMin = 50;
	static float rsunMax = 150;
	
	static float rBH = 100;
	static float mBH = (float) Math.pow(3 * rBH, 3);
	
	static float trackCut = (float) 0.05;	// only track high ratios of m/R^2
	
	// set the background stars
	static ArrayList<Point> stars = new ArrayList<Point>();
	static ArrayList<Integer> starBrightness = new ArrayList<Integer>();
	
	// All object positions / velocities and accelerations for gravitational interacting objects
	static public ArrayList<Point2D.Float> objects = new ArrayList<Point2D.Float>();
	static public ArrayList<Point2D.Float> objectVelocity = new ArrayList<Point2D.Float>();
	static public ArrayList<Point2D.Float> objectAcceleration = new ArrayList<Point2D.Float>();
	// trajObj will be a matrix for objects that interact which each other (based on high m/r^2 ratio)
	static public ArrayList<ArrayList<Integer>> trajObj = new ArrayList<ArrayList<Integer>>();
	// stores if an object is still existing (true / false)
	static public ArrayList<Boolean> objectState = new ArrayList<Boolean>();
	// radius, mass and label of the object
	static public ArrayList<Float> rObj = new ArrayList<Float>();
	static public ArrayList<Float> mObj = new ArrayList<Float>();
	static public ArrayList<String> label = new ArrayList<String>();
	static Random random =  new Random();
		
	public static void initGame(Game game) {
			System.out.println(nStars);
			// Place the background stars
			Random random = new Random();
			for (int i = 0; i < nStars; i++) {
				starBrightness.add(random.nextInt(200));
				stars.add(new Point(random.nextInt(game.dim.width), random.nextInt(game.dim.height)));
				
			}
			
			// Initializing the black hole
			float xyBH = (float) 0.5 * worldSize;
			objects.add(new Point2D.Float(xyBH, xyBH));
			objectVelocity.add(new Point2D.Float(0, 0));
			objectAcceleration.add(new Point2D.Float(0, 0));
			objectState.add(true);
			rObj.add(rBH);
			mObj.add(mBH);
			label.add("bh");
			
			for (int i = 0; i < nSun; i++) {
				
				float rSun = rsunMin + (rsunMax - rsunMin) * random.nextFloat();
				float mSun = (float) Math.pow(rSun, 3);
				
				float x = worldSize * random.nextFloat();
				float y = worldSize * random.nextFloat();
				
				float startVel = (float) Math.sqrt(mBH * game.G);
				
				objects.add(new Point2D.Float(x, y));
				objectState.add(true);
	
				float disx = x - xyBH;
				float disy = y - xyBH;
				float dis = (float) Math.sqrt(disx * disx + disy * disy);
				float vel = (float) (startVel / Math.sqrt(dis));
				float vx = (float) Math.sqrt(1 / (1 + Math.pow(disx / disy, 2))) * vel;
				float vy = - vx * disx / disy;
	
				objectVelocity.add(new Point2D.Float(vx, vy));
				objectAcceleration.add(new Point2D.Float(0, 0));
				
				rObj.add(rSun);
				mObj.add(mSun);
				label.add("sun");
			}
	
			for (int i = 0; i < nPlanets; i++) {
	
				float x = worldSize * random.nextFloat();
				float y = worldSize * random.nextFloat();
				
				float rSun = 0;
				float xSun = 0;
				float ySun = 0;
				float vxSun = 0;
				float vySun = 0;
				
				boolean join = false;
				for (int j = 0; j < nSun; j++) {
					if (join == true)
						continue;
					xSun = objects.get(j+nBH).x;
					ySun = objects.get(j+nBH).y;
					vxSun = objectVelocity.get(j+nBH).x;
					vySun = objectVelocity.get(j+nBH).y;
					rSun = rObj.get(j+nBH);
					float d = (float) Math.sqrt((xSun-x)*(xSun-x) + (ySun-y)*(ySun-y));
					float p = (float) Math.exp(-d / rPlanetScale);
					if (random.nextFloat() < p && d > rSun + rPlanetMax)
						join = true;
				}
				
				if (join == false) {
					i -= 1;
					continue;
				}
					
				
				float mSun = (float) Math.pow(rSun, 3);
				float startVel = (float) Math.sqrt(mSun * game.G);
				
				objects.add(new Point2D.Float(x, y));
				objectState.add(true);
	
				float disx = x - xSun;
				float disy = y - ySun;
				float dis = (float) Math.sqrt(disx * disx + disy * disy);
				float vel = (float) (startVel / Math.sqrt(dis));
				float vx = (float) Math.sqrt(1 / (1 + Math.pow(disx / disy, 2))) * vel;
				float vy = - vx * disx / disy;
	
				objectVelocity.add(new Point2D.Float(vx+vxSun, vy+vySun));
				objectAcceleration.add(new Point2D.Float(0, 0));
				
				float r = (rPlanetMax - rPlanetMin) * random.nextFloat() + rPlanetMin;
				
				rObj.add(r);
				mObj.add((float) Math.pow(r, 3));
				label.add("planet");
			}
			
			// Initializing the ship start parameters
			game.ship.x = worldSize * random.nextFloat(); 
			game.ship.y = worldSize * random.nextFloat();
			game.shipVelocity.x = 0;
			game.shipVelocity.y = 0;
			
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
	
				float diffxShip = game.ship.x - xi;
				float diffyShip = game.ship.y - yi;
				float disShip = (float) Math.sqrt(diffxShip * diffxShip + diffyShip * diffyShip);
				if (mObj.get(i) / Math.pow(disShip, 2) > 0.5 * trackCut || disShip <= 2 * rObj.get(i))
					game.trajShip.add(i);
			}
		}
}	