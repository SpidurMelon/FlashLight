package nl.fl.world;

import java.awt.Color;
import java.awt.Graphics2D;

import en.lib.collision.Collideable;

public class Block extends Collideable {
	public boolean solid = false;
	private static double blockWidth = 100, blockHeight = 100;
	public Block(int x, int y, boolean solid) {
		super((int)(x*blockWidth), (int)(y*blockHeight), (int)(blockWidth+getExtraWidth(x)), (int)(blockHeight+getExtraHeight(y)));
		this.solid = solid;
	}
	public static void setBlockWidth(double width) {
		blockWidth = width;
	}
	public static void setBlockHeight(double height) {
		blockHeight = height;
	}
	private static int getExtraWidth(int x) {
		if (getDecimal(blockWidth) > getDecimal((x+1)*blockWidth)) {
			return 1;
		} else {
			return 0;
		}
	}
	private static int getExtraHeight(int y) {
		if (getDecimal(blockHeight) > getDecimal((y+1)*blockHeight)) {
			return 1;
		} else {
			return 0;
		}
	}
	public static double getDecimal(double number) {
		return number-((int)number);
	}
	public static int getBlockWidth() {
		return (int)blockWidth;
	}
	public static int getBlockHeight() {
		return (int)blockHeight;
	}
	public void draw(Graphics2D g2) {
		if (solid) {
			g2.setColor(Color.BLACK);
			g2.fill(this);
		}
	}
}
