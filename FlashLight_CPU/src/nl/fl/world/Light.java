package nl.fl.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;

import nl.fl.main.MainFL;

public class Light {
	protected Point source;
	
	protected boolean directional = false;
	protected int reach, width, direction;
	
	private Color[] alphaGradient = {new Color(0,0,0,255), new Color(0,0,0,0)};
	private RadialGradientPaint lightGradient;
	public Light(Point source, int reach, int brightness) {
		this.reach = (int)(reach*MainFL.widthScale);
		setLocation(source);
		setBrightness(brightness);
	}
	
	public Light(Point source, int direction, int reach, int width, int brightness) {
		this(source, reach, brightness);
		this.direction = direction;
		this.width = width;
		directional = true;
	}
	
	public Point getSource() {
		return source;
	}
	
	public void setLocation(Point p) {
		this.source = p;
		lightGradient = new RadialGradientPaint(source, reach, new float[] {0, 1}, alphaGradient);
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public void setBrightness(int brightness) {
		alphaGradient[0] = new Color(0, 0, 0, brightness);
		lightGradient = new RadialGradientPaint(source, reach, new float[] {0, 1}, alphaGradient);
	}
	
	public void draw(Graphics2D g2) {
		g2.setPaint(lightGradient);
		if (directional) {
			g2.fillArc(source.x-reach, source.y-reach, reach*2, reach*2, direction-width/2, width);
		} else {
			g2.fillOval(source.x-reach, source.y-reach, reach*2, reach*2);
		}
		//
		//g2.setColor(Color.BLACK);
		/*
		for (int i = 0; i < amountOfRings; i++) {
			int currentLightSize = (int)(reach-i*ringWidth);
			float ringAlpha = endAlpha+((startAlpha-endAlpha)/amountOfRings)*i;
			g2.setComposite(ringComposite.derive(ringAlpha));
			if (directional) {
				g2.fillArc(	source.x-currentLightSize/2, source.y-currentLightSize/2, currentLightSize, currentLightSize, 
							direction-width/2, width);
			} else {
				g2.fillOval(source.x-currentLightSize/2, source.y-currentLightSize/2, currentLightSize, currentLightSize);
			}
		}
		*/
	}
}
