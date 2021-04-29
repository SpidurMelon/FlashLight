package nl.fl.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Pattern;

import en.lib.drawing.DrawUtils;
import en.lib.io.IO;
import nl.fl.entities.Moveable;
import nl.fl.entities.Player;
import nl.fl.main.MainFL;
import nl.fl.panels.DrawPanel;

public class World {
	public String stageDirectory;
	
	public Stage startingStage;
	public Point startingIPoint;
	
	public Stage currentStage;
	public Stage[] currentSurroundingStages = new Stage[4];
	
	public World(String stageDirectory) {
		this.stageDirectory = stageDirectory;
	}
	
	public void postInit() {
		String startConfig = IO.readFile(stageDirectory+ "/Start");
				
		startingStage = new Stage(IO.findFiles(stageDirectory, IO.getPropertyValue("Stage", startConfig)).get(0));
		switchStage(startingStage);
		
		String[] startCoords = IO.getPropertyValue("Coords", startConfig).split(",");
		startingIPoint = new Point(Integer.valueOf(startCoords[0]), Integer.valueOf(startCoords[1]));
	}
	
	public void moveStage(int direction) {
		switchStage(currentSurroundingStages[direction]);
	}
	
	private void switchStage(Stage stage) {
		currentStage = stage;
		currentSurroundingStages = currentStage.getSurroundingStages();
	}
	
	public Point getStart(Player player) {
		int startX = (int)(startingIPoint.x*Block.getBlockWidth()+Block.getBlockWidth()/2-player.width/2);
		int startY = (int)(startingIPoint.y*Block.getBlockHeight()+Block.getBlockHeight()/2-player.height/2);
		return new Point(startX, startY);
	}
	
	private ArrayList<Stage> stages = new ArrayList<Stage>();

	public Stage getStage(int x, int y) {
		for (Stage s:stages) {
			if (s.getX() == x && s.getY() == y) {
				return s;
			}
		}
		Stage newStage = new Stage(x, y, stageDirectory);
		stages.add(newStage);
		return newStage;
	}
	
	public void applyCollision(Moveable... moveables) {
		currentStage.applyCollision(moveables);
	}
	public Block[] getUnorderedBlocks() {
		return currentStage.getUnorderedBlocks();
	}
	public ArrayList<Block> getSolidBlocks() {
		return currentStage.getSolidBlocks();
	}
	
	public void draw(Graphics2D g2) {
		currentStage.draw(g2);
	}
}
