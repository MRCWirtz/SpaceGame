package main;

public class Physics {
	
	static Game game = Game.game;

	public static void move() {	
		for (int cnt = 0; cnt < Universe.planetSystems.size(); cnt++) {
			Universe.planetSystems.get(cnt).move();
		}
	}
}
