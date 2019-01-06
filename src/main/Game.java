package main;

import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Game implements ActionListener, KeyListener, MouseListener, MouseWheelListener {

	public JFrame jframe;
	public RenderPanel renderPanel;
	public Timer timer = new Timer(1, this);

	static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	public static Dimension dim = new Dimension(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());

	public static int tick = 0;
	public static boolean over = false;
	public static float scale = 1;

	public static float xCenter, yCenter;
	public static int predictor = 1000;

	public static int turn = 0;
	public static float radarSize = 300;

	public static boolean followMode = true;
	public static Integer followObject = -1;

	static Ship ship = new Ship();
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
		jframe.addMouseListener(this);
		jframe.addMouseWheelListener(this);
		
		Universe.initGame();

		timer.start();
	}


	public void actionPerformed(ActionEvent arg0) {

		tick++;

		if (followMode == true) {
			if (followObject == -1) {
				ship.scale();
				xCenter = ship.getX();
				yCenter = ship.getY();
			}
			else {
				Object currObject = Universe.planetSystems.getObject(followObject);
				xCenter = currObject.getX();
				yCenter = currObject.getY();
			}
		}
		else
			UserInteraction.interactiveMode();

		Universe.planetSystems.move();
		ship.move();
		renderPanel.repaint();

		if (over == true)
			timer.stop();
	}

	public static void main(String[] args){
		 game = new Game();
	}

	@Override
	public void keyPressed(KeyEvent ke) { UserInteraction.keyPressed(ke); }

	@Override
	public void keyReleased(KeyEvent ke) { UserInteraction.keyReleased(ke); }

	@Override
	public void keyTyped(KeyEvent ke) { UserInteraction.keyTyped(ke); }

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) { UserInteraction.mouseWheelMoved(e); }

	@Override
	public void mouseClicked(MouseEvent e) { MouseInput.mouseClicked(e); }


	@Override
	public void mouseEntered(MouseEvent e) { MouseInput.mouseEntered(e); }


	@Override
	public void mouseExited(MouseEvent e) { MouseInput.mouseExited(e); }


	@Override
	public void mousePressed(MouseEvent e) { MouseInput.mousePressed(e); }

	@Override
	public void mouseReleased(MouseEvent e) { MouseInput.mouseReleased(e); }
}
