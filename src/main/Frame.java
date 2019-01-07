package main;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Frame {
	
	public static float scale = 1;
	public static boolean followMode = true;
	public static int followObject = -1;

	public static float autoScaleNorm = 80;		// larger values increase the overall zoom
	public static float autoScaleRmin = (float) 1.2;	// in units of radius object
	public static float autoScaleRmax = 3;	// in units of radius object
	public static int autoScaleDead = 0;	// dead time for auto scale (activates after manual zooming)
	public static float autoScaleSpeed = (float) 0.01;

	public static float xCenter, yCenter;

	private static float xCenterTarget, yCenterTarget;
	
	private static float selectionAngle;
	private static float selectionR;
	private static BufferedImage selectionSprite;
	private static BufferedImage selectionSpriteScaled;
	
	public static void Camera( ) {
		if (followMode == true) {
			if (followObject == -1) {
				autoScale();
				xCenterTarget = Game.ship.getX();
				yCenterTarget = Game.ship.getY();
			}
			else {
				Object currObject = Universe.planetSystems.getObject(followObject);
				xCenterTarget = currObject.getX();
				yCenterTarget = currObject.getY();
			}
			if (Calculation.getDistance(xCenter, yCenter, xCenterTarget, yCenterTarget) < 3) {
				xCenter = xCenterTarget;
				yCenter = yCenterTarget;
			}
			else {
				xCenter += (xCenterTarget - xCenter) / 10;
				yCenter += (yCenterTarget - yCenter) / 10;
			}
		}
		else
			UserInteraction.interactiveMode();
	}
	
	public static void autoScale() {

		float rmin, rmax;
		autoScaleDead = Math.max(autoScaleDead - 1, 0);
		if (autoScaleDead > 0)
			return;

		for (int j = 0; j < Universe.planetSystems.n; j++) {
			Object currObject =  Universe.planetSystems.getObject(j);
			rmax = autoScaleRmax * currObject.getR();
			rmin = (float) autoScaleRmin * currObject.getR();
			float disj = Game.ship.distanceObject(currObject);
			float targetScale = 1 - rmax / (rmin - rmax) + disj / (rmin - rmax);
			if (disj > rmin & disj < rmax) {
				if (Game.ship.checkVel(j))
					Frame.scale += (targetScale * (autoScaleNorm / currObject.getR()) - Frame.scale) * autoScaleSpeed;
			}
		}
	}
	
	public static void drawSelection(Graphics g, ImageObserver io) {

		if (followObject == -1)
			selectionR = (float) 1.2 * 20;
		else
			selectionR = (float) 1.2 * Universe.planetSystems.getObject(followObject).getR() * scale;
		
		if (followMode == false || selectionR * scale > 100)
			return;

		try {
			if (selectionR < 50)
				selectionSprite = ImageIO.read(new File(RenderPanel.imagePath + "selection.png"));
			else
				selectionSprite = ImageIO.read(new File(RenderPanel.imagePath + "selection_big.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		selectionAngle += 0.01;
		selectionSpriteScaled = RenderPanel.rescaleImage(selectionSprite, selectionR / (selectionSprite.getWidth() / 2));
		AffineTransform tx = AffineTransform.getRotateInstance(selectionAngle, selectionR, selectionR);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		float xcoord = (xCenterTarget - xCenter) * scale + Game.dim.width / 2 - selectionR;
		float ycoord = (yCenterTarget - yCenter) * scale + Game.dim.height / 2 - selectionR;
		g.drawImage(op.filter(selectionSpriteScaled, null), (int) xcoord, (int) ycoord, io);
	}
}
