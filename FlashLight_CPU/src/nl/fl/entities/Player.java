package nl.fl.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import en.lib.input.KeyBinding;
import en.lib.math.Vector;
import en.lib.setup.Panel;
import nl.fl.main.MainFL;
import nl.fl.panels.DrawPanel;

public class Player extends Moveable {
	private ArrayList<String> keysHeld = new ArrayList<String>();
	
	private double startingSpeed = 0.5*MainFL.widthScale, maxSpeed = 5*MainFL.widthScale;
	private Vector gravity = new Vector(90, 0.25*MainFL.heightScale);
	private Vector jump = new Vector(270, 11*MainFL.heightScale);
	
	private int collisionChecks = 10;
	
	private int startX, startY;
	public Player(int startX, int startY, int width, int height, Panel parent) {
		super(startX, startY, width, height);
		this.startX = startX;
		this.startY = startY;
		bindKeys(parent);
	}
	
	public Player(Point start, int width, int height, Panel parent) {
		this(start.x, start.y, width, height, parent);
	}
	
	public Player(int width, int height, Panel parent) {
		this(0, 0, width, height, parent);
	}
	
	public void tick(double delta) {
		if (keysHeld.contains("D")) {
			movement.setXComp(maxSpeed);
		}
		if (keysHeld.contains("A")) {
			movement.setXComp(-maxSpeed);
		}
		
		if (!keysHeld.contains("A") && !keysHeld.contains("D")) {
			movement.setXComp(movement.getXComp()-movement.getXComp()*0.10*delta);
		}
		
		if (movement.getYComp() < -0.2 || movement.getYComp() > 0.2) {
			inAir = true;
		}
		if (keysHeld.contains("W") && !inAir) {
			movement = movement.addVector(jump, 1);
			inAir = true;
		}
		
		movement = movement.addVector(gravity, delta);
		
		for (int i = 0; i < collisionChecks; i++) {
			x+=movement.getXComp()*(delta/collisionChecks);
			y+=movement.getYComp()*(delta/collisionChecks);
			DrawPanel.world.applyCollision(this);
		}
	}
	
	public void draw(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		g2.fill(this);
	}
	
	public void bindKeys(Panel parent) {
		new KeyBinding(KeyEvent.VK_W, parent, false) {
			public void onAction() {
				if (!keysHeld.contains("W")) {
					keysHeld.add("W");
				}
			}
		};
		new KeyBinding(KeyEvent.VK_W, parent, true) {
			public void onAction() {
				if (keysHeld.contains("W")) {
					keysHeld.remove("W");
				}
			}
		};
		new KeyBinding(KeyEvent.VK_A, parent, false) {
			public void onAction() {
				if (!keysHeld.contains("A")) {
					keysHeld.add("A");
				}
			}
		};
		new KeyBinding(KeyEvent.VK_A, parent, true) {
			public void onAction() {
				if (keysHeld.contains("A")) {
					keysHeld.remove("A");
				}
			}
		};
		new KeyBinding(KeyEvent.VK_S, parent, false) {
			public void onAction() {
				if (!keysHeld.contains("S")) {
					keysHeld.add("S");
				}
			}
		};
		new KeyBinding(KeyEvent.VK_S, parent, true) {
			public void onAction() {
				if (keysHeld.contains("S")) {
					keysHeld.remove("S");
				}
			}
		};
		new KeyBinding(KeyEvent.VK_D, parent, false) {
			public void onAction() {
				if (!keysHeld.contains("D")) {
					keysHeld.add("D");
				}
			}
		};
		new KeyBinding(KeyEvent.VK_D, parent, true) {
			public void onAction() {
				if (keysHeld.contains("D")) {
					keysHeld.remove("D");
				}
			}
		};
	}
}
