package main;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Physics {
	
	static Setup set = Setup.setup;
		
	public static void move() {
		
		for (int i = 0; i < set.objects.size(); i++) {

			if (i == set.turn & set.objectState.get(i) == true)
				updateTrack();
			else if (i == set.turn)
				set.turn += 1;
			
			if (set.objectState.get(i) == false) 
				continue;
			
			float x = set.objects.get(i).x;
			float y = set.objects.get(i).y;
			float velX = set.objectVelocity.get(i).x;
			float velY = set.objectVelocity.get(i).y;
			float m = set.mObj.get(i);
			float r = set.rObj.get(i);
			
			for (int j: set.trajObj.get(i)) {
				if (j == i || set.objectState.get(j) == false)
					continue;

				float xj = set.objects.get(j).x;
				float yj = set.objects.get(j).y;

				float diffxj = xj - x;
				float diffyj = yj - y;
				float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
				float mj = set.mObj.get(j);
				
				if (disj < set.rObj.get(j) + set.rObj.get(i) && j > i) {
					
					velX = (m * velX + mj * set.objectVelocity.get(j).x) / (m + mj);
					velY = (m * velY + mj * set.objectVelocity.get(j).y) / (m + mj);
					
					x = (m * x + mj * xj) / (m + mj);
					y = (m * y + mj * yj) / (m + mj);
					
					m += mj;
					float rj = set.rObj.get(j);
					r = (float) Math.pow(Math.pow(r, 3) + Math.pow(rj, 3), (float) 1/ 3);
					if (r > set.rsunMin)
						set.label.set(i, "sun");

					set.mObj.set(i, m);
					set.rObj.set(i, r);
					set.objectState.set(j, false);
					
					continue;
				}
				
				velX += set.G * mj * diffxj / Math.pow(disj, 3);
				velY += set.G * mj * diffyj / Math.pow(disj, 3);
			}

			set.objectAcceleration.set(i, new Point2D.Float(velX - set.objectVelocity.get(i).x, velY - set.objectVelocity.get(i).y));
			set.objectVelocity.set(i, new Point2D.Float(velX, velY));
			set.objects.set(i, new Point2D.Float(x + velX, y + velY));
		}
	}
	
	public static void updateTrack() {
		
		int i = set.turn;
		float x = set.objects.get(i).x;
		float y = set.objects.get(i).y;
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		for (int j = 0; j < set.objects.size(); j++) {
			
			if (j == i)
				continue;
			
			float xi = set.objects.get(i).x;
			float yi = set.objects.get(i).y;

			float diffxj = x - xi;
			float diffyj = y - yi;
			float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
			if (set.mObj.get(j) / Math.pow(disj, 2) > set.trackCut || disj <= 2 * (set.rObj.get(i) + set.rObj.get(j)))
					indices.add(j);
		}
		set.trajObj.set(i, indices);
		if (set.turn + 1 >= set.objects.size())
			set.turn = 0;
		else
			set.turn += 1;
	}
}
