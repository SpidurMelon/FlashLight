package tdtest.entities;

import java.nio.FloatBuffer;

import org.joml.Vector2f;

public class Light {
	public static final int dataSize = 5;
	public Vector2f position;
	public float angle, width, radius, brightness;
	public boolean on = true;
	public Light(Vector2f position, float angle, float width, float radius, float brightness) {
		this.position = position;
		this.angle = angle;
		this.width = width;
		this.radius = radius;
		this.brightness = brightness;
	}
	public void getPosition(FloatBuffer target) {
		target.put(position.x);
		target.put(position.y);
	}
	public void getData(FloatBuffer target) {
		target.put(angle);
		target.put(width);
		target.put(radius);
		if (on) {
			target.put(brightness);
		} else {
			target.put(0);
		}
	}
	public String toString() {
		return "(Position=(" + position.x + "," + position.y + "), Angle=" + angle + ", Width=" + width + ", Radius=" + radius + ", Brightness= " + brightness + ")";
	}
}
