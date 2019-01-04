package main;

import java.awt.event.KeyEvent;

public class UserInteraction {
	
	static Game game = Game.game;

	public static void setup() {
		if (game.shipMode == false) {
			if (game.keys[KeyEvent.VK_W])
				game.yCenter -= 5 / game.scale;
			if (game.keys[KeyEvent.VK_S])
				game.yCenter += 5 / game.scale;
			if (game.keys[KeyEvent.VK_A])
				game.xCenter -= 5 / game.scale;
			if (game.keys[KeyEvent.VK_D])
				game.xCenter += 5 / game.scale;
		}
	}

}
