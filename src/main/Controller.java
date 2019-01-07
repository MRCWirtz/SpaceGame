package main;

import java.awt.Graphics;
import java.util.LinkedList;

public class Controller {

	private LinkedList<Bullet> b = new LinkedList<Bullet>();
	
	Bullet TempBullet;
	
	Game game;
	
	public Controller(Game game) {
		this.game = game;
	}
	
	public void update() {
		for (int i = 0; i < b.size(); i++) {
			TempBullet = b.get(i);
			TempBullet.update();
		}
	}
	
	public void draw(Graphics g) {
		for (int i = 0; i < b.size(); i++) {
			TempBullet = b.get(i);
			if (TempBullet.travelled > 5000)
				removeBullet(TempBullet);
			TempBullet.draw(g);
		}
	}
	
	public void addBullet(Bullet block) { b.add(block); }
	
	public void removeBullet(Bullet block) { b.remove(block); }
	
}
