package main;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Physics {
	
	static Game game = Game.game;
		
	public static void move() {
		
		for (int i = 0; i < game.objects.size(); i++) {

			if (i == game.turn & game.objectState.get(i) == true)
				// update for this object i, the interaction matrice with other objects
				updateInteractionMatrice();
			else if (i == game.turn)
				game.turn += 1;
			
			if (game.objectState.get(i) == false) 
				continue;
			
			float x = game.objects.get(i).x;
			float y = game.objects.get(i).y;
			float velX = game.objectVelocity.get(i).x;
			float velY = game.objectVelocity.get(i).y;
			float m = game.mObj.get(i);
			float r = game.rObj.get(i);
			
			for (int j: game.trajObj.get(i)) {
				if (j == i || game.objectState.get(j) == false)
					continue;

				float xj = game.objects.get(j).x;
				float yj = game.objects.get(j).y;

				float diffxj = xj - x;
				float diffyj = yj - y;
				float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
				float mj = game.mObj.get(j);
				
				// check for a collision of two objects
				if (disj < game.rObj.get(j) + game.rObj.get(i) && j > i) {
					
					velX = (m * velX + mj * game.objectVelocity.get(j).x) / (m + mj);
					velY = (m * velY + mj * game.objectVelocity.get(j).y) / (m + mj);
					
					x = (m * x + mj * xj) / (m + mj);
					y = (m * y + mj * yj) / (m + mj);
					
					m += mj;
					float rj = game.rObj.get(j);
					r = (float) Math.pow(Math.pow(r, 3) + Math.pow(rj, 3), (float) 1/ 3);
					if (r > game.rsunMin)
						game.label.set(i, "sun");

					game.mObj.set(i, m);
					game.rObj.set(i, r);
					game.objectState.set(j, false);
					
					continue;
				}
				
				velX += game.G * mj * diffxj / Math.pow(disj, 3);
				velY += game.G * mj * diffyj / Math.pow(disj, 3);
			}

			game.objectAcceleration.set(i, new Point2D.Float(velX - game.objectVelocity.get(i).x, velY - game.objectVelocity.get(i).y));
			game.objectVelocity.set(i, new Point2D.Float(velX, velY));
			game.objects.set(i, new Point2D.Float(x + velX, y + velY));
		}
	}
	
	public static void updateInteractionMatrice() {
		
		int i = game.turn;
		float x = game.objects.get(i).x;
		float y = game.objects.get(i).y;
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		for (int j = 0; j < game.objects.size(); j++) {
			
			if (j == i)
				continue;
			
			float xi = game.objects.get(i).x;
			float yi = game.objects.get(i).y;

			float diffxj = x - xi;
			float diffyj = y - yi;
			float disj = (float) Math.sqrt(diffxj * diffxj + diffyj * diffyj);
			if (game.mObj.get(j) / Math.pow(disj, 2) > game.trackCut || disj <= 2 * (game.rObj.get(i) + game.rObj.get(j)))
					indices.add(j);
		}
		game.trajObj.set(i, indices);
		if (game.turn + 1 >= game.objects.size())
			game.turn = 0;
		else
			game.turn += 1;
	}
}
