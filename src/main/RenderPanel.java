package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
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
	
	public BufferedImage ship;
	public BufferedImage shipRadar;
	public String imagePath = System.getProperty("user.dir") + "/img/";

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		Game game = Game.game;
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, game.dim.width, game.dim.height);
		
		for (int i = 0; i < game.stars.size(); i++) {
			g.setColor(new Color(255, 255, 255, game.starBrightness.get(i)));
			g.fillOval(game.stars.get(i).x, game.stars.get(i).y, 2, 2);
		}
		
		g.setColor(Color.GRAY);
		for (int i = 0; i < game.objects.size(); i++) {
			if (game.objectState.get(i) == false)
				continue;
			float r = game.rObj.get(i) * game.scale;
			float xRel = (game.objects.get(i).x - game.xCenter) * game.scale;
			float yRel = (game.objects.get(i).y - game.yCenter) * game.scale;
			if (game.label.get(i) == "sun")
				g.setColor(Color.YELLOW);
			if (game.label.get(i) == "planet")
				g.setColor(Color.GRAY);
			if (game.label.get(i) == "bh")
				g.setColor(Color.BLACK);
			g.fillOval((int) (xRel + game.dim.width / 2 - r), (int) (yRel + game.dim.height / 2 - r), (int) (2*r), (int) (2*r));
		}

		// draw the ships trajectory
		if (game.shipMode == true) {
			float x = game.ship.x;
			float y = game.ship.y;
			float xold = game.ship.x;
			float yold = game.ship.y;
			float vx = game.shipVelocity.x;
			float vy = game.shipVelocity.y;
	
			ArrayList<Point2D.Float> objectCopy = new ArrayList<Point2D.Float>();
			ArrayList<Point2D.Float> objectVelCopy = new ArrayList<Point2D.Float>();
			for (int j = 0; j < game.objects.size(); j++) {
				
				float xj = game.objects.get(j).x;
				float yj = game.objects.get(j).y;
				float vxj = game.objectVelocity.get(j).x;
				float vyj = game.objectVelocity.get(j).y;
				objectCopy.add(j, new Point2D.Float(xj, yj));
				objectVelCopy.add(j, new Point2D.Float(vxj, vyj));
			}
			
			float length = 0; 
			
			for (int timeStep = 0; timeStep < game.predictor; timeStep++) {
			
				for (int j = 0; j < game.objects.size(); j++) {
					
					float xj = objectCopy.get(j).x;
					float yj = objectCopy.get(j).y;
					float vxj = objectVelCopy.get(j).x;
					float vyj = objectVelCopy.get(j).y;
					float axj = game.objectAcceleration.get(j).x;
					float ayj = game.objectAcceleration.get(j).y;
			
					float diffxj = xj - x;
					float diffyj = yj - y;
					float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
					float mj = game.mObj.get(j);
					float rj = game.rObj.get(j);
		
					vx += game.G * mj * diffxj / Math.pow(disj, 3);
					vy += game.G * mj * diffyj / Math.pow(disj, 3);
					
					if (disj < rj) {
						String gameover = "Expecting collision!";
						g.setColor(Color.RED);
						g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
						g.drawString(gameover, game.dim.width / 2 - 100, game.dim.height - 100);
					}
					vxj += axj;
					vyj += ayj;
					objectVelCopy.set(j, new Point2D.Float(vxj, vyj));
					objectCopy.set(j, new Point2D.Float(xj + vxj, yj + vyj));
				}
				x += vx;
				y += vy;
	
				if (length > 10) {
					int trans = (int) ((int) - 400 * timeStep * (timeStep - game.predictor) / Math.pow(game.predictor, 2));
					g.setColor(new Color(255, 255, 51, trans));
					float xRel = (x - game.ship.x) * game.scale;
					float yRel = (y - game.ship.y) * game.scale;
					float xRelold = (x - game.ship.x) * game.scale;
					float yRelold = (y - game.ship.y) * game.scale;
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
			if (game.keys[KeyEvent.VK_W] & game.shipMode == true)
				ship = ImageIO.read(new File(imagePath + "spacecraftAcc.gif"));
			else
				ship = ImageIO.read(new File(imagePath + "spacecraft.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AffineTransform tx = AffineTransform.getRotateInstance(game.shipAngle, ship.getWidth() / 2, ship.getHeight() / 2);
		AffineTransform txRadar = AffineTransform.getRotateInstance(game.shipAngle, shipRadar.getWidth() / 2, shipRadar.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		AffineTransformOp opRadar = new AffineTransformOp(txRadar, AffineTransformOp.TYPE_BILINEAR);
		
		float xcoord = (game.ship.x - game.xCenter) * game.scale + game.dim.width / 2 - ship.getWidth() / 2;
		float ycoord = (game.ship.y - game.yCenter) * game.scale + game.dim.height / 2 - ship.getHeight() / 2;
		g.drawImage(op.filter(ship, null), (int) xcoord, (int) ycoord, this);
		
		// draw the radar
		g.setColor(new Color(255, 255, 255, 30));
		float xRadar = game.dim.width - game.radarSize - 20;
		float yRadar = 20;
		g.fillRect((int) xRadar, (int) yRadar, (int) game.radarSize, (int) game.radarSize);
		
		g.drawImage(opRadar.filter(shipRadar, null), (int) (xRadar + ((float) game.ship.x / ((float) game.worldSize)) * game.radarSize - shipRadar.getWidth() / 2), 
				(int) (yRadar + ((float) game.ship.y / ((float) game.worldSize)) * game.radarSize - shipRadar.getHeight() / 2), this);

		for (int i = 0; i < game.objects.size(); i++){
			if (game.label.get(i) == "planet" || game.objectState.get(i) == false)
				continue;
			if (game.label.get(i) == "bh")
				g.setColor(Color.BLACK);
			else
				g.setColor(new Color(255, 255, 51, 100));
				
			g.fillOval((int) (xRadar + ((float) game.objects.get(i).x / ((float) game.worldSize)) * game.radarSize - 3), 
					(int) (yRadar + ((float) game.objects.get(i).y / ((float) game.worldSize)) * game.radarSize - 3), 6, 6);
		}
		
		if (game.shipMode == true) {
			String info = "Press [Esc] to switch view.";
			g.setColor(Color.GRAY);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
			g.drawString(info, 20, 20);
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
