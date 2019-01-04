package main;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Game implements ActionListener, KeyListener, MouseWheelListener {
	

	public JFrame jframe;
	public RenderPanel renderPanel;
	public Physics physics;
	public Timer timer = new Timer(1, this);
	public Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	
	public boolean over = false;
	public int tick = 0;	
	public float scale = 1;
	public float worldSize = 10000;
	
	public int predictor = 1000;
	public float trackCut = (float) 0.05;	// only track high ratios of m/R^2
	public int turn = 0;
	public float radarSize = 300;

	boolean[] keys = new boolean[222];

	// gravitational constant
	public float G = (float) ((float) 3 * Math.pow(10, -4));

	// set the universe
	public int nStars = 5000;
	public int nBH = 1;
	public int nSun = 6;
	public int nPlanets = 50;
	
	public float rPlanetMin = 5;
	public float rPlanetMax = 20;
	public float rPlanetScale = 500;
	
	public float rsunMin = 50;
	public float rsunMax = 150;
	
	public float rBH = 100;
	public float mBH = (float) Math.pow(3 * rBH, 3);
	
	// set the background stars
	public ArrayList<Point> stars = new ArrayList<Point>();
	public ArrayList<Integer> starBrightness = new ArrayList<Integer>();
	
	// All object positions / velocities and accelerations for gravitational interacting objects
	public ArrayList<Point2D.Float> objects = new ArrayList<Point2D.Float>();
	public ArrayList<Point2D.Float> objectVelocity = new ArrayList<Point2D.Float>();
	public ArrayList<Point2D.Float> objectAcceleration = new ArrayList<Point2D.Float>();
	// trajObj will be a matrice for objects that interact which each other (based on high m/r^2 ratio)
	public ArrayList<ArrayList<Integer>> trajObj = new ArrayList<ArrayList<Integer>>();
	// stores if an object is still existing (true / false)
	public ArrayList<Boolean> objectState = new ArrayList<Boolean>();
	// radius, mass and label of the object
	public ArrayList<Float> rObj = new ArrayList<Float>();
	public ArrayList<Float> mObj = new ArrayList<Float>();
	public ArrayList<String> label = new ArrayList<String>();

	// coordinate, velocity and trajectory prediction of the space ship 
	public Point2D.Float ship = new Point2D.Float();
	public Point2D.Float shipVelocity = new Point2D.Float();
	public ArrayList<Integer> trajShip = new ArrayList<Integer>();
	// parameters
	public float shipAcceleration = (float) 0.001;
	public float shipAngle = 0;
	public float rotSpeed = (float) 0.03;
	public boolean turbo = false;
	public boolean shipMode = true;
	public float xCenter, yCenter;
	
	public Random random;
	public static Game game;
	
	public Game() {
		jframe = new JFrame("Planet System");
		jframe.setSize(dim.width, dim.height);
		jframe.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		jframe.setUndecorated(true);
		jframe.setVisible(true);
		jframe.add(renderPanel = new RenderPanel());
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.addKeyListener(this);
		initGame();
		timer.start();
	}

	public void initGame() {

		random = new Random();
		System.out.println(nStars);
		// Place the background stars
		for (int i = 0; i < nStars; i++) {
			starBrightness.add(random.nextInt(200));
			stars.add(new Point(random.nextInt(dim.width), random.nextInt(dim.height)));
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
			
			float startVel = (float) Math.sqrt(mBH * G);
			
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
			float startVel = (float) Math.sqrt(mSun * G);
			
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
		ship.x = worldSize * random.nextFloat(); 
		ship.y = worldSize * random.nextFloat();
		shipVelocity.x = 0;
		shipVelocity.y = 0;
		
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

			float diffxShip = ship.x - xi;
			float diffyShip = ship.y - yi;
			float disShip = (float) Math.sqrt(diffxShip * diffxShip + diffyShip * diffyShip);
			if (mObj.get(i) / Math.pow(disShip, 2) > 0.5 * trackCut || disShip <= 2 * rObj.get(i))
				trajShip.add(i);
		}
	}
	
	public void actionPerformed(ActionEvent arg0) {

		tick++;
		
		if (shipMode == true) {
			xCenter = ship.x;
			yCenter = ship.y;
		}
		
		Physics.move();
		Ship.move();
		if (shipMode == true)
			Ship.scale();
		else
			UserInteraction.setup();
		renderPanel.repaint();
		
		if (over == true)
			timer.stop();
		
	}
	
	public static void main(String[] args){
		game = new Game();
	}
	
	@Override
	public void keyPressed(KeyEvent ke) {
		if (ke.getExtendedKeyCode() == KeyEvent.VK_ESCAPE)
			shipMode = !shipMode;
		if (ke.getExtendedKeyCode() == KeyEvent.VK_PLUS || ke.getExtendedKeyCode() == KeyEvent.VK_PAGE_UP) 
			scale *= 1.25;
		if (ke.getExtendedKeyCode() == KeyEvent.VK_MINUS || ke.getExtendedKeyCode() == KeyEvent.VK_PAGE_DOWN)
			scale /= 1.25;
	    keys[ke.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent ke) {
	    keys[ke.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent ke) {
	}
	
	public void mouseWheelMoved(MouseWheelEvent ke) {
	    if (ke.isControlDown()) {
	        if (ke.getWheelRotation() < 0)
	        	scale *= 1.25;
	        else
	        	scale /= 1.25;
	    }
	}
}
