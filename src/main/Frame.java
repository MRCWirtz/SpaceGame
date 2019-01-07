package main;

public class Frame {
	
	public static float scale = 1;

	public static float autoScaleNorm = 80;		// larger values increase the overall zoom
	public static float autoScaleRmin = (float) 1.2;	// in units of radius object
	public static float autoScaleRmax = 3;	// in units of radius object
	public static int autoScaleDead = 0;	// dead time for auto scale (activates after manual zooming)
	public static float autoScaleSpeed = (float) 0.01;

	public static float xCenter, yCenter;
	public static boolean followMode = true;
	public static int followObject = -1;
	
	public static void Camera( ) {
		if (followMode == true) {
			if (followObject == -1) {
				autoScale();
				xCenter = Game.ship.getX();
				yCenter = Game.ship.getY();
			}
			else {
				Object currObject = Universe.planetSystems.getObject(followObject);
				xCenter = currObject.getX();
				yCenter = currObject.getY();
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
}
