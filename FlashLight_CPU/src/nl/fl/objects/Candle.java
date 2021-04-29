package nl.fl.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import nl.fl.main.MainFL;
import nl.fl.world.Light;

public class Candle extends Lamp {
	private Ellipse2D.Double shape;
	
	public Candle(Ellipse2D.Double shape, int reach, int brightness) {
		super(getEllipseCenter(shape), reach, brightness);
		this.shape = shape;
	}
	
	public Candle(int x, int y, int width, int height, int reach, int brightness) {
		this(new Ellipse2D.Double(x-width*MainFL.widthScale/2, y-height*MainFL.heightScale/2, width*MainFL.widthScale, height*MainFL.heightScale), reach, brightness);
	}
	
	public void drawLight(Graphics2D g2) {
		if (on) {
			light.draw(g2);
		}
	}
	
	private static Color transparentBlack = new Color(0, 0, 0, 100);
	public void drawLamp(Graphics2D g2) {
		if (on) {
			g2.setColor(Color.YELLOW);
			g2.fill(shape);
		}
		g2.setColor(Color.BLACK);
		g2.fillRect((int)(shape.x+shape.width/4), (int)shape.getMaxY(), (int)(shape.width/2), (int)shape.height);
		g2.setColor(transparentBlack);
		g2.fillOval((int)(shape.x), (int)(shape.getMaxY()+shape.height-shape.width/2), (int)(shape.width), (int)(shape.width));
	}
}
