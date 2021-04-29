package nl.fl.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;

import nl.fl.main.MainFL;
import nl.fl.world.Light;

public class HangLamp extends Lamp {
	private Ellipse2D.Double shape;
	private Light aura;
	
	public HangLamp(Ellipse2D.Double shape, int width, int reach, int brightness) {
		super(getEllipseCenter(shape), -90, reach, width, brightness);
		this.shape = shape;
		aura = new Light(getEllipseCenter(shape), (int)(shape.width*5), 50);
	}
	
	public HangLamp(int x, int y, int width, int height, int lightWidth, int reach, int brightness) {
		this(new Ellipse2D.Double(x-width*MainFL.widthScale/2, y-height*MainFL.heightScale/2, width*MainFL.widthScale, height*MainFL.heightScale), lightWidth, reach, brightness);
	}
	
	private static final BasicStroke defaultStroke = new BasicStroke(1);
	private static final BasicStroke ropeSize = new BasicStroke(3);
	private static final double hatSize = 2;
	public void drawLight(Graphics2D g2) {
		if (on) {
			light.draw(g2);
			aura.draw(g2);
		}
	}
	
	public void drawLamp(Graphics2D g2) {
		if (on) {
			g2.setColor(Color.YELLOW);
			g2.fill(shape);
		}
		g2.setColor(Color.BLACK);
		g2.fill(new Polygon(new int[]{light.getSource().x, (int)(light.getSource().x+shape.width*hatSize), (int)(light.getSource().x-shape.width*hatSize)}, 
							new int[]{(int)(light.getSource().y-shape.height*hatSize), light.getSource().y, light.getSource().y}, 3));
		g2.setStroke(ropeSize);
		g2.drawLine(light.getSource().x, (int)(light.getSource().y-shape.height*hatSize), 
					light.getSource().x, (int)(light.getSource().y-shape.height*hatSize)-999);
		g2.setStroke(defaultStroke);
	}
}
