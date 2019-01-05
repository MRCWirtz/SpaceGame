package main;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Physics {
	
	static Game game = Game.game;
	
		
	public static void move() {
		
		for (int i = 0; i < Universe.objects.size(); i++) {
			System.out.println(Universe.objects.size());
			if (i == game.turn & Universe.objectState.get(i) == true)
				// update for this object i the interaction matrix with other objects
				updateInteractionMatrix();
			else if (i == game.turn)
				game.turn += 1;
			
			if (Universe.objectState.get(i) == false) 
				continue;
			
			float x = Universe.objects.get(i).x;
			float y = Universe.objects.get(i).y;
			float velX = Universe.objectVelocity.get(i).x;
			float velY = Universe.objectVelocity.get(i).y;
			float m = Universe.mObj.get(i);
			float r = Universe.rObj.get(i);
			
			for (int j: Universe.trajObj.get(i)) {
				if (j == i || Universe.objectState.get(j) == false)
					continue;

				float xj = Universe.objects.get(j).x;
				float yj = Universe.objects.get(j).y;

				float diffxj = xj - x;
				float diffyj = yj - y;
				float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
				float mj = Universe.mObj.get(j);
				
				// check for a collision of two objects
				if (disj < Universe.rObj.get(j) + Universe.rObj.get(i) && j > i) {
					
					velX = (m * velX + mj * Universe.objectVelocity.get(j).x) / (m + mj);
					velY = (m * velY + mj * Universe.objectVelocity.get(j).y) / (m + mj);
					
					x = (m * x + mj * xj) / (m + mj);
					y = (m * y + mj * yj) / (m + mj);
					
					m += mj;
					float rj = Universe.rObj.get(j);
					r = (float) Math.pow(Math.pow(r, 3) + Math.pow(rj, 3), (float) 1/ 3);
					if (r > Universe.rsunMin)
						Universe.label.set(i, "sun");

					Universe.mObj.set(i, m);
					Universe.rObj.set(i, r);
					Universe.objectState.set(j, false);
					
					continue;
				}
				
				velX += game.G * mj * diffxj / Math.pow(disj, 3);
				velY += game.G * mj * diffyj / Math.pow(disj, 3);
			}

			Universe.objectAcceleration.set(i, new Point2D.Float(velX - Universe.objectVelocity.get(i).x, velY - Universe.objectVelocity.get(i).y));
			Universe.objectVelocity.set(i, new Point2D.Float(velX, velY));
			Universe.objects.set(i, new Point2D.Float(x + velX, y + velY));
		}
	}
	
	public static void updateInteractionMatrix() {
		
		int i = game.turn;
		float x = Universe.objects.get(i).x;
		float y = Universe.objects.get(i).y;
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		for (int j = 0; j < Universe.objects.size(); j++) {
			
			if (j == i)
				continue;
			
			float xi = Universe.objects.get(i).x;
			float yi = Universe.objects.get(i).y;

			float diffxj = x - xi;
			float diffyj = y - yi;
			float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
			if (Universe.mObj.get(j) / Math.pow(disj, 2) > Universe.trackCut || disj <= 2 * (Universe.rObj.get(i) + Universe.rObj.get(j)))
					indices.add(j);
		}
		Universe.trajObj.set(i, indices);
		if (game.turn + 1 >= Universe.objects.size())
			game.turn = 0;
		else
			game.turn += 1;
	}
}
