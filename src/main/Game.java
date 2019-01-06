package main;

import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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

	public float xCenter, yCenter;
	public int predictor = 1000;

	public int turn = 0;
	public float radarSize = 300;

	boolean[] keys = new boolean[222];

	// gravitational constant
	public float G = (float) ((float) 3 * Math.pow(10, -4));
	public boolean flightMode = true;




	Ship ship = new Ship();
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

		if (flightMode == true) {
			xCenter = ship.getX();
			yCenter = ship.getY();
		}

		Physics.move();
		ship.move(game);
		if (flightMode == true)
			scale();
		else
			UserInteraction.setup();
		renderPanel.repaint();

		if (over == true)
			timer.stop();

	}

	public  void scale() {

		float rc = 200;
		float rmax = 10;
		game.scale = 1;

		for (int j = 0; j < Universe.objects.size(); j++) {

			float xj = Universe.objects.get(j).x;
			float yj = Universe.objects.get(j).y;

			float diffxj = xj - ship.getX();
			float diffyj = yj - ship.getY();
			float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);

			if (disj < rmax) {
				game.scale = 2;
				break;
			}
			if (disj < rc) {
				if (ship.checkVel(j)) {
					game.scale = 2 - 1 * (disj - rmax) / 200;
					rc = disj;
				}
			}
		}
		//System.out.println(game.scale);
	}

	public static void main(String[] args){
		 game = new Game();
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		if (ke.getExtendedKeyCode() == KeyEvent.VK_ESCAPE)
			flightMode = !flightMode;
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
