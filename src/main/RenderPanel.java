package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class RenderPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public BufferedImage shipSprite;
	public BufferedImage shipRadar;
	public String imagePath = System.getProperty("user.dir") + "/img/";

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		Game game = Game.game;
		Ship ship = game.ship;

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, game.dim.width, game.dim.height);

		for (int i = 0; i < Universe.stars.size(); i++) {
			g.setColor(new Color(255, 255, 255, Universe.starBrightness.get(i)));
			g.fillOval(Universe.stars.get(i).x, Universe.stars.get(i).y, 2, 2);
		}

		g.setColor(Color.GRAY);
		for (int cnt = 0; cnt < Universe.planetSystems.n; cnt++) {
			Object currObject = Universe.planetSystems.getObject(cnt);
			float xObj = currObject.getX();
			float yObj = currObject.getY();
			float r = currObject.getR() * game.scale;
			float xRel = (xObj - game.xCenter) * game.scale;
			float yRel = (yObj - game.yCenter) * game.scale;
			if (currObject.isPlanet == true)
				g.setColor(Color.GRAY);
			else
				g.setColor(Color.YELLOW);
			g.fillOval((int) (xRel + game.dim.width / 2 - r), (int) (yRel + game.dim.height / 2 - r), (int) (2*r), (int) (2*r));
		}

		// draw the ships trajectory
		if (game.flightMode == true) {
			float x = ship.getX();
			float y = ship.getY();
			float xold = ship.getX();
			float yold = ship.getY();
			float vx = ship.getVx();
			float vy = ship.getVy();

			float length = 0;
			for (int timeStep = 0; timeStep < game.predictor; timeStep++) {

				for (int j = 0; j < Universe.planetSystems.n; j++) {
					
					Object currObject = Universe.planetSystems.getObject(j);
					float diffxj = currObject.getXFuture(timeStep) - x;
					float diffyj = currObject.getYFuture(timeStep) - y;
					float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);

					vx += game.G * currObject.getMass() * diffxj / Math.pow(disj, 3);
					vy += game.G * currObject.getMass() * diffyj / Math.pow(disj, 3);

					if (disj < currObject.getR()) {
						String gameover = "Expecting collision!";
						g.setColor(Color.RED);
						g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
						g.drawString(gameover, game.dim.width / 2 - 100, game.dim.height - 100);
					}
				}
				x += vx;
				y += vy;

				if (length > 10) {
					int trans = (int) ((int) - 400 * timeStep * (timeStep - game.predictor) / Math.pow(game.predictor, 2));
					g.setColor(new Color(255, 255, 51, trans));
					float xRel = (x - ship.getX()) * game.scale;
					float yRel = (y - ship.getY()) * game.scale;
					float xRelold = (x - ship.getX()) * game.scale;
					float yRelold = (y - ship.getY()) * game.scale;
					g.drawLine((int) (xRelold + game.dim.width / 2), (int) (yRelold + game.dim.height / 2), (int) (xRel + game.dim.width / 2), (int) (yRel + game.dim.height / 2));
				}

				length += Math.sqrt((x - xold) * (x - xold) + (y - yold) * (y - yold));
				xold = x;
				yold = y;
			}
		}

		// draw the spacecraft
		try {
			shipRadar = ImageIO.read(new File(imagePath + "spacecraftRadar.gif"));
			if (game.keys[KeyEvent.VK_W] & game.flightMode == true & ship.isTurbo == true & ship.getFuelLevel() > 0)
				shipSprite = ImageIO.read(new File(imagePath + "spacecraftTurbo.gif"));
			else if (game.keys[KeyEvent.VK_W] & game.flightMode == true & ship.getFuelLevel() > 0)
				shipSprite = ImageIO.read(new File(imagePath + "spacecraftAcc.gif"));
			else
				shipSprite = ImageIO.read(new File(imagePath + "spacecraft.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		AffineTransform tx = AffineTransform.getRotateInstance(ship.getShipAngle(), shipSprite.getWidth() / 2, shipSprite.getHeight() / 2);
		AffineTransform txRadar = AffineTransform.getRotateInstance(ship.getShipAngle(), shipRadar.getWidth() / 2, shipRadar.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		AffineTransformOp opRadar = new AffineTransformOp(txRadar, AffineTransformOp.TYPE_BILINEAR);

		float xcoord = (ship.getX() - game.xCenter) * game.scale + game.dim.width / 2 - shipSprite.getWidth() / 2;
		float ycoord = (ship.getY() - game.yCenter) * game.scale + game.dim.height / 2 - shipSprite.getHeight() / 2;
		g.drawImage(op.filter(shipSprite, null), (int) xcoord, (int) ycoord, this);
		
		// draw the fuel level
		g.setColor(Color.GREEN);
		g.fillRect(20, (int) (0.96 * game.dim.height), (int) (400 * ship.getFuelLevel()), (int) (0.02 * game.dim.height));
		
		Graphics2D g2 = (Graphics2D) g;
		float thickness = 4;
		Stroke oldStroke = g2.getStroke();
		g2.setColor(Color.ORANGE);
		g2.setStroke(new BasicStroke(thickness));
		g.drawRect(20, (int) (0.96 * game.dim.height), 400, (int) (0.02 * game.dim.height));
		g2.setStroke(oldStroke);

		// draw the radar
		g.setColor(new Color(255, 255, 255, 30));
		float xRadar = game.dim.width - game.radarSize - 20;
		float yRadar = 20;
		g.fillRect((int) xRadar, (int) yRadar, (int) game.radarSize, (int) game.radarSize);

		g.drawImage(opRadar.filter(shipRadar, null), (int) (xRadar + ((float) ship.getX() / ((float) Universe.worldSize)) * game.radarSize - shipRadar.getWidth() / 2),
				(int) (yRadar + ((float) ship.getY() / ((float) Universe.worldSize)) * game.radarSize - shipRadar.getHeight() / 2), this);

		g.setColor(new Color(255, 255, 51, 100));
		for (int cnt = 0; cnt < Universe.planetSystems.systemIdx.size(); cnt++) {
			Object currObject = Universe.planetSystems.getObject(Universe.planetSystems.systemIdx.get(cnt));
			float xSystem = currObject.getX();
			float ySystem = currObject.getY();
			g.fillOval((int) (xRadar + ((float) xSystem / ((float) Universe.worldSize)) * game.radarSize - 3),
					(int) (yRadar + ((float) ySystem / ((float) Universe.worldSize)) * game.radarSize - 3), 6, 6);
		}

		if (game.flightMode == true) {
			String info = "Press [Esc] to switch view.";
			g.setColor(Color.GRAY);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
			g.drawString(info, 20, 20);

			String speed = Double.toString(ship.getVabs());
			g.setColor(Color.BLUE);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
			g.drawString(speed, 1000, 20);

		}
		else {
			String info = "Press [-] or [Page Down] to zoom out.";
			g.setColor(Color.GRAY);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
			g.drawString(info, 20, 20);
		}

		if (game.over == true) {
			String gameover = "GAME OVER!";
			g.setColor(Color.RED);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
			g.drawString(gameover, game.dim.width / 2 - 18 * gameover.length(), game.dim.height / 2 + 30);
		}

	}
}
