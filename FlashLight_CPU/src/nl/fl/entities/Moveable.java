package nl.fl.entities;

import java.awt.Point;

import en.lib.collision.Collideable;
import en.lib.math.Vector;

public abstract class Moveable extends Collideable {
	public Vector movement = new Vector();
	public boolean inAir = false;
	public Moveable(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void moveTo(Point p) {
		moveTo(p.x, p.y);
	}
}
