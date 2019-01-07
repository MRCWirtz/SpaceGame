package main;

public class Calculation {
	
	public static float getDistance(float x1, float y1, float x2, float y2) {
		float diffX = x1 - x2;
		float diffY = y1 - y2;
		return (float) Math.sqrt(diffX * diffX + diffY * diffY);
	}

	public static float[] coord2pixel(float x, float y, float scale) {
		float xRel = (x - Frame.xCenter) * Frame.scale;
		float yRel = (y - Frame.yCenter) * Frame.scale;
		return new float[] {xRel + Game.dim.width / 2, yRel + Game.dim.height / 2};
	}
	
}
