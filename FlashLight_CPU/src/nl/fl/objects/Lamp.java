package nl.fl.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import nl.fl.world.Light;

public class Lamp {
	public Light light;
	protected boolean on = true;
	public Lamp(Point source, int reach, int brightness) {
		light = new Light(source, reach, brightness);
	}
	public Lamp(Point source, int direction, int reach, int width, int brightness) {
		light = new Light(source, direction, reach, width, brightness);
	}
	public Lamp(int x, int y, int reach, int brightness) {
		this(new Point(x, y), reach, brightness);
	}
	public Lamp(int x, int y, int direction, int reach, int width, int brightness) {
		this(new Point(x, y), direction, reach, width, brightness);
	}
	public void turnOn() {
		on = true;
	}
	
	public void turnOff() {
		on = false;
	}
	public void switchState() {
		on = !on;
	}
	private static int lampSize = 10;
	public void drawLamp(Graphics2D g2) {
		//g2.setColor(Color.YELLOW);
		//g2.fillOval(light.getSource().x-lampSize/2, light.getSource().y-lampSize/2, lampSize, lampSize);
	}
	
	public void drawLight(Graphics2D g2) {
		if (on) {
			light.draw(g2);
		}
	}
	
	public static Point getEllipseCenter(Ellipse2D.Double shape) {
		return new Point((int)shape.getCenterX(), (int)shape.getCenterY());
	}
}
