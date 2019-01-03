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
		System.out.println(imagePath);

		super.paintComponent(g);
		Setup set = Setup.setup;
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, set.dim.width, set.dim.height);
		
		for (int i = 0; i < set.stars.size(); i++) {
			g.setColor(new Color(255, 255, 255, set.starBrightness.get(i)));
			g.fillOval(set.stars.get(i).x, set.stars.get(i).y, 2, 2);
		}
		
		g.setColor(Color.GRAY);
		for (int i = 0; i < set.objects.size(); i++) {
			if (set.objectState.get(i) == false)
				continue;
			float r = set.rObj.get(i) * set.scale;
			float xRel = (set.objects.get(i).x - set.ship.x) * set.scale;
			float yRel = (set.objects.get(i).y - set.ship.y) * set.scale;
			if (set.label.get(i) == "sun")
				g.setColor(Color.YELLOW);
			if (set.label.get(i) == "planet")
				g.setColor(Color.GRAY);
			if (set.label.get(i) == "bh")
				g.setColor(Color.BLACK);
			g.fillOval((int) (xRel + set.dim.width / 2 - r), (int) (yRel + set.dim.height / 2 - r), (int) (2*r), (int) (2*r));
		}
		
		float x = set.ship.x;
		float y = set.ship.y;
		float xold = set.ship.x;
		float yold = set.ship.y;
		float vx = set.shipVelocity.x;
		float vy = set.shipVelocity.y;

		ArrayList<Point2D.Float> objectCopy = new ArrayList<Point2D.Float>();
		ArrayList<Point2D.Float> objectVelCopy = new ArrayList<Point2D.Float>();
		for (int j = 0; j < set.objects.size(); j++) {
			
			float xj = set.objects.get(j).x;
			float yj = set.objects.get(j).y;
			float vxj = set.objectVelocity.get(j).x;
			float vyj = set.objectVelocity.get(j).y;
			objectCopy.add(j, new Point2D.Float(xj, yj));
			objectVelCopy.add(j, new Point2D.Float(vxj, vyj));
		}
		
		float length = 0; 
		
		for (int timeStep = 0; timeStep < set.predictor; timeStep++) {
		
			for (int j = 0; j < set.objects.size(); j++) {
				
				float xj = objectCopy.get(j).x;
				float yj = objectCopy.get(j).y;
				float vxj = objectVelCopy.get(j).x;
				float vyj = objectVelCopy.get(j).y;
				float axj = set.objectAcceleration.get(j).x;
				float ayj = set.objectAcceleration.get(j).y;
		
				float diffxj = xj - x;
				float diffyj = yj - y;
				float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
				float mj = set.mObj.get(j);
				float rj = set.rObj.get(j);
	
				vx += set.G * mj * diffxj / Math.pow(disj, 3);
				vy += set.G * mj * diffyj / Math.pow(disj, 3);
				
				if (disj < rj) {
					String gameover = "Expecting collision!";
					g.setColor(Color.RED);
					g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
					g.drawString(gameover, set.dim.width / 2 - 100, set.dim.height - 100);
				}
				vxj += axj;
				vyj += ayj;
				objectVelCopy.set(j, new Point2D.Float(vxj, vyj));
				objectCopy.set(j, new Point2D.Float(xj + vxj, yj + vyj));
			}
			x += vx;
			y += vy;

			if (length > 10) {
				int trans = (int) ((int) - 400 * timeStep * (timeStep - set.predictor) / Math.pow(set.predictor, 2));
				g.setColor(new Color(255, 255, 51, trans));
				float xRel = (x - set.ship.x) * set.scale;
				float yRel = (y - set.ship.y) * set.scale;
				float xRelold = (x - set.ship.x) * set.scale;
				float yRelold = (y - set.ship.y) * set.scale;
				g.drawLine((int) (xRelold + set.dim.width / 2), (int) (yRelold + set.dim.height / 2), (int) (xRel + set.dim.width / 2), (int) (yRel + set.dim.height / 2));
			}
			
			length += Math.sqrt((x - xold) * (x - xold) + (y - yold) * (y - yold));
			xold = x;
			yold = y;
		}

		try {
			shipRadar = ImageIO.read(new File(imagePath + "spacecraftRadar.gif"));
			if (set.keys[KeyEvent.VK_W])
				ship = ImageIO.read(new File(imagePath + "spacecraftAcc.gif"));
			else
				ship = ImageIO.read(new File(imagePath + "spacecraft.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AffineTransform tx = AffineTransform.getRotateInstance(set.shipAngle, ship.getWidth() / 2, ship.getHeight() / 2);
		AffineTransform txRadar = AffineTransform.getRotateInstance(set.shipAngle, shipRadar.getWidth() / 2, shipRadar.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		AffineTransformOp opRadar = new AffineTransformOp(txRadar, AffineTransformOp.TYPE_BILINEAR);
		
		g.drawImage(op.filter(ship, null), set.dim.width / 2 - ship.getWidth() / 2, set.dim.height / 2 - ship.getHeight() / 2, this);
		
		g.setColor(new Color(255, 255, 255, 30));
		float xRadar = set.dim.width - set.radarSize - 20;
		float yRadar = 20;
		g.fillRect((int) xRadar, (int) yRadar, (int) set.radarSize, (int) set.radarSize);
		
		g.drawImage(opRadar.filter(shipRadar, null), (int) (xRadar + ((float) set.ship.x / ((float) set.worldSize)) * set.radarSize - shipRadar.getWidth() / 2), 
				(int) (yRadar + ((float) set.ship.y / ((float) set.worldSize)) * set.radarSize - shipRadar.getHeight() / 2), this);

		for (int i = 0; i < set.objects.size(); i++){
			if (set.label.get(i) == "planet" || set.objectState.get(i) == false)
				continue;
			if (set.label.get(i) == "bh")
				g.setColor(Color.BLACK);
			else
				g.setColor(new Color(255, 255, 51, 100));
				
			g.fillOval((int) (xRadar + ((float) set.objects.get(i).x / ((float) set.worldSize)) * set.radarSize - 3), 
					(int) (yRadar + ((float) set.objects.get(i).y / ((float) set.worldSize)) * set.radarSize - 3), 6, 6);
		}
		
		if (set.over == true) {
			String gameover = "GAME OVER!";
			g.setColor(Color.RED);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
			g.drawString(gameover, set.dim.width / 2 - 18 * gameover.length(), set.dim.height / 2 + 30);
		}
			
	}
}
