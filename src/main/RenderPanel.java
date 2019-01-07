package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.BasicStroke;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;


public class RenderPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public static BufferedImage shipSprite;
	public static BufferedImage shipRadar;
	public static String imagePath = System.getProperty("user.dir") + "/img/";

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Game.dim.width, Game.dim.height);

		for (int i = 0; i < Universe.stars.size(); i++) {
			g.setColor(new Color(255, 255, 255, Universe.starBrightness.get(i)));
			g.fillOval(Universe.stars.get(i).x, Universe.stars.get(i).y, 2, 2);
		}

		g.setColor(Color.GRAY);
		for (int cnt = 0; cnt < Universe.planetSystems.n; cnt++) {
			Object currObject = Universe.planetSystems.getObject(cnt);
			float r = currObject.getR() * Frame.scale;
			float[] pixel = Calculation.coord2pixel(currObject.getX(), currObject.getY(), Frame.scale);
			if (currObject.isPlanet == true)
				g.setColor(Color.GRAY);
			else
				g.setColor(Color.YELLOW);
			g.fillOval((int) (pixel[0] - r), (int) (pixel[1] - r), (int) (2*r), (int) (2*r));
		}

		// draw the ships trajectory
		float x = Game.ship.getX();
		float y = Game.ship.getY();
		float xold = Game.ship.getX();
		float yold = Game.ship.getY();
		float vx = Game.ship.getVx();
		float vy = Game.ship.getVy();

		float length = 0;
		int expectCollision = -1;
		int expectCollisionTime = -1;
		for (int timeStep = 0; timeStep < Game.predictor; timeStep++) {

			for (int j = 0; j < Universe.planetSystems.n; j++) {
				
				Object currObject = Universe.planetSystems.getObject(j);
				float diffxj = currObject.getXFuture(timeStep) - x;
				float diffyj = currObject.getYFuture(timeStep) - y;
				float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);

				vx += Physics.G * currObject.getMass() * diffxj / Math.pow(disj, 3);
				vy += Physics.G * currObject.getMass() * diffyj / Math.pow(disj, 3);

				if (disj < currObject.getR() & expectCollision == -1) {
					String gameover = "Expecting collision!";
					g.setColor(Color.RED);
					g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
					g.drawString(gameover, Game.dim.width / 2 - 100, Game.dim.height - 100);
					expectCollision = j;
					expectCollisionTime = timeStep;
					timeStep = Game.predictor + 1;
				}
			}
			x += vx;
			y += vy;

			if (length > 10) {
				int trans = (int) ((int) - 400 * timeStep * (timeStep - Game.predictor) / Math.pow(Game.predictor, 2));
				g.setColor(new Color(255, 255, 51, trans));
				float xRel = (x - Frame.xCenter) * Frame.scale;
				float yRel = (y - Frame.yCenter) * Frame.scale;
				float xRelold = (x - Frame.xCenter) * Frame.scale;
				float yRelold = (y - Frame.yCenter) * Frame.scale;
				g.drawLine((int) (xRelold + Game.dim.width / 2), (int) (yRelold + Game.dim.height / 2), (int) (xRel + Game.dim.width / 2), (int) (yRel + Game.dim.height / 2));
			}

			length += Math.sqrt((x - xold) * (x - xold) + (y - yold) * (y - yold));
			xold = x;
			yold = y;
		}
		if (expectCollision >= 0) {
			Object currObject = Universe.planetSystems.getObject(expectCollision);
			g.setColor(new Color(255, 0, 0, 150));
			for (int timeStepObject = 0; timeStepObject < expectCollisionTime / 4; timeStepObject++) {
				int realTime = 4 * timeStepObject;
				float [] pixel1 = Calculation.coord2pixel(currObject.getXFuture(realTime), currObject.getYFuture(realTime), Frame.scale);
				float [] pixel2 = Calculation.coord2pixel(currObject.getXFuture(realTime + 1), currObject.getYFuture(realTime + 1), Frame.scale);
				g.drawLine((int) pixel1[0], (int) pixel1[1], (int) pixel2[0], (int) pixel2[1]);
			}
			float [] pixelCollision = Calculation.coord2pixel(currObject.getXFuture(expectCollisionTime), currObject.getYFuture(expectCollisionTime), Frame.scale);
			g.fillOval((int) pixelCollision[0]-1, (int) pixelCollision[1]-1, 2, 2);
			float r = currObject.getR() * Frame.scale; 
			g.drawOval((int) (pixelCollision[0] - r), (int) (pixelCollision[1] - r), 2 * (int) r, 2 * (int) r);
		}

		// draw the spacecraft
		Game.ship.draw(g, this);
		
		// Draw bullets
		Game.controller.draw(g);
		
		Frame.drawSelection(g, this);
		
		// draw the fuel level
		g.setColor(Color.GREEN);
		g.fillRect(20, (int) (0.96 * Game.dim.height), (int) (400 * Game.ship.getFuelLevel()), (int) (0.02 * Game.dim.height));
		
		Graphics2D g2 = (Graphics2D) g;
		float thickness = 4;
		Stroke oldStroke = g2.getStroke();
		g2.setColor(Color.ORANGE);
		g2.setStroke(new BasicStroke(thickness));
		g.drawRect(20, (int) (0.96 * Game.dim.height), 400, (int) (0.02 * Game.dim.height));
		g2.setStroke(oldStroke);

		// draw info text
		if (Frame.followMode == true) {
			String info = "Press [Esc] to switch view.";
			g.setColor(Color.GRAY);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
			g.drawString(info, 20, 20);

			String speed = String.format("V = %.2f km/s", Game.ship.getVabs());
			g.setColor(Color.BLUE);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 18));
			g.drawString(speed, Game.dim.width / 2 -20, 20);

		}
		else {
			String info = "Press [-] or [Page Down] to zoom out.";
			g.setColor(Color.GRAY);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
			g.drawString(info, 20, 20);
		}

		if (Game.over == true) {
			String gameover = "GAME OVER!";
			g.setColor(Color.RED);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
			g.drawString(gameover, Game.dim.width / 2 - 18 * gameover.length(), Game.dim.height / 2 + 30);
		}
	}
	
	public static BufferedImage rescaleImage(BufferedImage img, float scale) {
		BufferedImage after = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return scaleOp.filter(img, after);
	}
}
