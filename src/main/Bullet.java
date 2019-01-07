package main;

import java.awt.Color;
import java.awt.Graphics;

public class Bullet {
	
	private float x;
	private float y;
	private float vx;
	private float vy;
	private float speed;
	public int travelled;
	
	public Bullet(float x, float y, float angle, float leftright, Game game) {
		this.vx = (float) Math.sin(angle);
		this.vy = - (float) Math.cos(angle);
		this.x = x + 20 * vx + 2 * leftright * (float) Math.cos(angle);
		this.y = y + 20 * vy + 2 * leftright * (float) Math.sin(angle);
		this.travelled = 0;
		this.speed = 20;
	}

	public void update() {
		x += speed * vx;
		y += speed * vy;
		travelled += speed;
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		float [] pixel1 = Calculation.coord2pixel(x, y, Frame.scale);
		float [] pixel2 = Calculation.coord2pixel(x + 5 * vx, y + 5 * vy, Frame.scale);
		g.drawLine((int) pixel1[0], (int) pixel1[1], (int) pixel2[0], (int) pixel2[1]);
	}
}
