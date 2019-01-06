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

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Game implements ActionListener, KeyListener, MouseWheelListener {
	

	public JFrame jframe;
	public RenderPanel renderPanel;
	public Universe universe;
	public Timer timer = new Timer(1, this);
	
	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	public Dimension dim = new Dimension(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
	
	public boolean over = false;
	public int tick = 0;	
	public float scale = 1;
	
	
	public int predictor = 1000;
	
	public int turn = 0;
	public float radarSize = 300;

	boolean[] keys = new boolean[222];

	// gravitational constant
	public float G = (float) ((float) 3 * Math.pow(10, -4));

	

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
		Universe.initGame(this);
		timer.start();
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
