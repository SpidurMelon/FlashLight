package tdtest.entities;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import tdlib.collision.Collision;
import tdlib.geometry.GeoUtils;
import tdlib.input.InputMaster;
import tdlib.objects.ColoredVertex;
import tdlib.utils.ColorUtils;
import tdlib.utils.ShapeUtils;
import tdtest.main.FlashLight;
import tdtest.main.World;

import static org.lwjgl.opengl.GL46.*;

import java.awt.Color;
import java.nio.FloatBuffer;

public class Player {
	private static final int collisionAccuracy = 3;
	private static final float movementSpeed = 0.4f, gravityStrength = 2f, jumpStrength = 1.3f;
	
	public Vector2f position = new Vector2f();
	public Vector2f velocity = new Vector2f(0, -10);
	public Vector2f size;
	
	private float flashBrightness = 0.3f, auraBrightness = 0.15f;
	public Light flashLight = new Light(position, 0, 0.3f, 0.7f, flashBrightness);
	public Light aura = new Light(position, 0, (float)(2*Math.PI), 0.2f, auraBrightness);
	private float auraLightMultiplier = 0;
	
	private boolean inAir = false;
	
	public int vbo;
	
	public Player(Vector2f size) {
		this.size = size;
		vbo = glGenBuffers();
		flashLight.on = false;
		aura.on = false;
		updateVBO();
	}
	public void tick(float delta) {
		if (InputMaster.isKeyPressed(GLFW.GLFW_KEY_D)) {
			velocity.x=movementSpeed;
		} else if (InputMaster.isKeyPressed(GLFW.GLFW_KEY_A)) {
			velocity.x=-movementSpeed;
		} else {
			velocity.x=0;
		}
		
		if (!inAir && (InputMaster.isKeyPressed(GLFW.GLFW_KEY_W) || InputMaster.isKeyPressed(GLFW.GLFW_KEY_SPACE))) {
			inAir = true;
			velocity.y = jumpStrength;
		}
		
		velocity.y -= gravityStrength*delta;
		boolean onGround = false;
		for (int i = 0; i < collisionAccuracy; i++) {
			applyVelocity(delta/collisionAccuracy);
			boolean[] directionsPushed = World.currentFloor.getCurrentRoom().requestCollision(this);
			if (directionsPushed[Collision.UP] && velocity.y < 0) {
				velocity.y = 0;
			} 
			if (directionsPushed[Collision.DOWN] && velocity.y > 0) {
				velocity.y = 0;
			}
			if (directionsPushed[Collision.UP]) {
				onGround = true;
			}
		}
		
		if (onGround) {
			inAir = false;
		} else {
			inAir = true;
		}
		
		Vector2f playerPos = new Vector2f(position.x+size.x/2, position.y+size.y/2);
		Vector2f mousePos =  new Vector2f(	(float)((InputMaster.getMouseLocation().x-FlashLight.frame.width/2)/FlashLight.frame.width)*2, 
											(float)((-InputMaster.getMouseLocation().y+FlashLight.frame.height/2)/FlashLight.frame.height)*2);
		flashLight.angle = calcAdjustedAngle(playerPos,mousePos);
		flashLight.position = playerPos;
		
		aura.position = playerPos;
		
		hardCodedEvents(delta);
		
		updateVBO();
	}
	public void hardCodedEvents(float delta) {
		if (!aura.on && World.currentFloor.getCurrentRoom().roomPos.equals(new Vector2i(12, -4))) {
			aura.on = true;
		}
		
		if (aura.on) {
			if (auraLightMultiplier < 1) {
				auraLightMultiplier += 0.1*delta;
				aura.brightness = auraBrightness*auraLightMultiplier;
			} else if (auraLightMultiplier > 1) {
				auraLightMultiplier = 1;
				aura.brightness = auraBrightness*auraLightMultiplier;
			}
		}
	}
	public static float calcAdjustedAngle(Vector2f point1, Vector2f point2) {
		float dx = point2.x-point1.x;
		float dy = (point2.y-point1.y)*(FlashLight.frame.height/(float)FlashLight.frame.width);
		float preAngle = (float)(Math.atan(dy/dx) + Math.PI/2);
		if (dx > 0) {
			preAngle += Math.PI;
		}
		return preAngle;
	}
	public void applyVelocity(float delta) {
		position.x += velocity.x*delta;
		position.y += velocity.y*delta;
	}
	public void updateVBO() {
		ColoredVertex[] vertices = new ColoredVertex[] {
			new ColoredVertex(position.x, position.y, 0, ColorUtils.BLACK),
			new ColoredVertex(position.x+size.x, position.y, 0, ColorUtils.BLACK),
			new ColoredVertex(position.x+size.x, position.y+size.y, 0, ColorUtils.BLACK),
			new ColoredVertex(position.x, position.y+size.y, 0, ColorUtils.BLACK)
		};
		FloatBuffer buffer = BufferUtils.createFloatBuffer(6*6);
		ShapeUtils.getColoredShape(vertices, ShapeUtils.square, buffer);
		buffer.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
