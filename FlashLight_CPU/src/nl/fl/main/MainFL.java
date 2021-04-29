package nl.fl.main;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import en.lib.setup.RegularFrame;
import en.lib.setup.Tick;
import en.lib.sounds.AudioMaster;
import nl.fl.panels.DrawPanel;

public class MainFL {
	private static int targetWidth = 1920, targetHeight = 1080;
	public static int WIDTH, HEIGHT;
	public static double widthScale, heightScale;
	public static DrawPanel drawPanel;
	
	private static Rectangle bounds;
	public static void main(String[] args) {
		AudioMaster.init();
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		WIDTH = gd.getDisplayMode().getWidth();
		HEIGHT = gd.getDisplayMode().getHeight();
		
		bounds = new Rectangle(0, 0, WIDTH, HEIGHT);
		
		widthScale = WIDTH/(double)targetWidth;
		heightScale = HEIGHT/(double)targetHeight;
		
		Tick.fullManualPainting = true;
		RegularFrame frame = new RegularFrame(WIDTH, HEIGHT, true);
		drawPanel = new DrawPanel();
		frame.addPanel(drawPanel, BorderLayout.CENTER);
	}
	
	public static Rectangle getBounds() {
		return bounds;
	}
	
	public static void quit() {
		AudioMaster.cleanUp();
		System.exit(0);
	}
}
