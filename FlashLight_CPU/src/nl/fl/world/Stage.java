package nl.fl.world;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import en.lib.io.IO;
import en.lib.math.Vector;
import nl.fl.entities.Moveable;
import nl.fl.main.MainFL;
import nl.fl.objects.Candle;
import nl.fl.objects.HangLamp;
import nl.fl.objects.Lamp;
import nl.fl.panels.DrawPanel;

public class Stage {
	public static final String blankStage = IO.readFile("resources/Blank");
	
	public String fileString;
	public Block[][] blocks;
	public ArrayList<Lamp> lamps = new ArrayList<Lamp>();
	private Stage[] surroundingStages;
	
	private int sx, sy;
	private String directory;
	
	public BufferedImage staticLightMap;
	
	public Stage(int sx, int sy, String directory) {
		this.sx = sx;
		this.sy = sy;
		this.directory = directory;
		initThread.start();
	}
	
	public Stage(String path) {
		int openBracketIndex = path.indexOf("(");
		int closingBracketIndex = path.indexOf(")");
		String[] coords = path.substring(openBracketIndex+1, closingBracketIndex).split(",");
		this.sx = Integer.valueOf(coords[0]);
		this.sy = Integer.valueOf(coords[1]);;
		init(IO.readFile(path));
	}
	
	public void init(String fileString) {
		this.fileString = fileString;
		
		int width = Integer.valueOf(IO.getPropertyValue("Width", fileString));
		int height = Integer.valueOf(IO.getPropertyValue("Height", fileString));
		
		Block.setBlockWidth(MainFL.WIDTH/(double)width);
		Block.setBlockHeight(MainFL.HEIGHT/(double)height);
		blocks = new Block[height][width];
		
		ArrayList<Integer> blockLayout = IO.readIntArray(IO.getPropertyValue("Layout", fileString));
		for (int y = 0; y < blocks.length; y++) {
			for (int x = 0; x < blocks[y].length; x++) {
				blocks[y][x] = new Block(x, y, blockLayout.get(y*blocks[y].length+x)==1);
			}
		}
		
		ArrayList<Integer> lamps = IO.readIntArray(IO.getPropertyValue("Lamps", fileString));
		if (lamps != null) {
			for (int y = 0; y < blocks.length; y++) {
				for (int x = 0; x < blocks[y].length; x++) {
					if (lamps.get(y*blocks[y].length+x) != 0) {
						String lampData = IO.getPropertyValue(String.valueOf(lamps.get(y*blocks[y].length+x)), fileString);
						String[] lampProperties = lampData.split(",");
						if (lampProperties[0].equals("Light")) {
							this.lamps.add(	new Lamp(x*Block.getBlockWidth()+Block.getBlockWidth()/2, y*Block.getBlockHeight()+Block.getBlockHeight()/2, Integer.valueOf(lampProperties[1]), Integer.valueOf(lampProperties[2]), 
											Integer.valueOf(lampProperties[3]), Integer.valueOf(lampProperties[4])));
						} else if (lampProperties[0].equals("OmniLight")) {
							this.lamps.add(	new Lamp(x*Block.getBlockWidth()+Block.getBlockWidth()/2, y*Block.getBlockHeight()+Block.getBlockHeight()/2, Integer.valueOf(lampProperties[1]), Integer.valueOf(lampProperties[2])));
						} else if (lampProperties[0].equals("Candle")) {
							this.lamps.add(	new Candle(x*Block.getBlockWidth()+Block.getBlockWidth()/2, y*Block.getBlockHeight()+Block.getBlockHeight()/2, Integer.valueOf(lampProperties[1]), Integer.valueOf(lampProperties[2]), 
											Integer.valueOf(lampProperties[3]), Integer.valueOf(lampProperties[4])));
						} else if (lampProperties[0].equals("HangLamp")) {
							this.lamps.add(	new HangLamp(x*Block.getBlockWidth()+Block.getBlockWidth()/2, y*Block.getBlockHeight()+Block.getBlockHeight()/2, Integer.valueOf(lampProperties[1]), Integer.valueOf(lampProperties[2]), 
											Integer.valueOf(lampProperties[3]), Integer.valueOf(lampProperties[4]), Integer.valueOf(lampProperties[5])));
						}
					}
				}
			}
		}
		
		
			if (!this.lamps.isEmpty()) {
				staticLightMap = new BufferedImage(MainFL.WIDTH, MainFL.HEIGHT, BufferedImage.TYPE_INT_ARGB);
				Graphics2D staticLightMapGraphics = staticLightMap.createGraphics();
					staticLightMapGraphics.setColor(Color.BLACK);
					staticLightMapGraphics.fillRect(0, 0, MainFL.WIDTH, MainFL.HEIGHT);
					staticLightMapGraphics.setComposite(AlphaComposite.DstOut);
				for (Lamp l:this.lamps) {
					l.drawLight(staticLightMapGraphics);
				}
				staticLightMapGraphics.dispose();
			}
		
	}
	private Thread initThread = new Thread(new Runnable() {
		public void run() {
			ArrayList<String> pathsToFiles = IO.findFiles(directory, "(" + sx + "," + sy + ")");
			if (!pathsToFiles.isEmpty()) {
				init(IO.readFile(pathsToFiles.get(0)));
			} else {
				init(blankStage);
			}
		}
	});
	
	public Stage[] getSurroundingStages() {
		while(fileString.isEmpty()) {
			
		}
		if (surroundingStages == null) {
			surroundingStages = new Stage[4];
			String antiChamber = IO.getPropertyValue("AntiChamber", fileString, ":");
			if (antiChamber != null && !antiChamber.isEmpty()) {
				String upChamber = IO.getPropertyValue("Up", antiChamber);
				if (upChamber != null) {
					String[] coordsUp = upChamber.split(",");
					surroundingStages[0] = DrawPanel.world.getStage(Integer.valueOf(coordsUp[0]), Integer.valueOf(coordsUp[1]));
				}
				String rightChamber = IO.getPropertyValue("Right", antiChamber);
				if (rightChamber != null) {
					String[] coordsRight = rightChamber.split(",");
					surroundingStages[1] = DrawPanel.world.getStage(Integer.valueOf(coordsRight[0]), Integer.valueOf(coordsRight[1]));
				}
				String downChamber = IO.getPropertyValue("Down", antiChamber);
				if (downChamber != null) {
					String[] coordsDown = downChamber.split(",");
					surroundingStages[2] = DrawPanel.world.getStage(Integer.valueOf(coordsDown[0]), Integer.valueOf(coordsDown[1]));
				}
				String leftChamber = IO.getPropertyValue("Left", antiChamber);
				if (leftChamber != null) {
					String[] coordsLeft = leftChamber.split(",");
					surroundingStages[3] = DrawPanel.world.getStage(Integer.valueOf(coordsLeft[0]), Integer.valueOf(coordsLeft[1]));
				}
			} 
			
			if (surroundingStages[0] == null) {
				surroundingStages[0] = DrawPanel.world.getStage(getX(), getY()-1);
			}
			if (surroundingStages[1] == null) {
				surroundingStages[1] = DrawPanel.world.getStage(getX()+1, getY());
			}
			if (surroundingStages[2] == null) {
				surroundingStages[2] = DrawPanel.world.getStage(getX(), getY()+1);
			}
			if (surroundingStages[3] == null) {
				surroundingStages[3] = DrawPanel.world.getStage(getX()-1, getY());
			}
		}
		
		return surroundingStages;
	}
	
	public int getX() {
		return this.sx;
	}
	public int getY() {
		return this.sy;
	}
	
	public void applyCollision(Moveable... moveables) {
		for (Moveable m:moveables) {
			for (int y = 0; y < blocks.length; y++) {
				for (int x = 0; x < blocks[y].length; x++) {
					if (blocks[y][x].solid) {
						Vector leastResistance = m.pathOfLeastResistance(blocks[y][x]);
						if (leastResistance.getSize() != 0) {
							
							if (leastResistance.getDirection() == 0 && (x == blocks[y].length-1 || blocks[y][x+1].solid)) {
								leastResistance.setSize(0);
							}
							if (leastResistance.getDirection() == 180 && (x == 0 || blocks[y][x-1].solid)) {
								leastResistance.setSize(0);
							}
							if (leastResistance.getDirection() == 90 && (y == blocks.length-1 || blocks[y+1][x].solid)) {
								leastResistance.setSize(0);
							}
							if (leastResistance.getDirection() == 270 && (y == 0 || blocks[y-1][x].solid)) {
								leastResistance.setSize(0);
							}
							
							if (leastResistance.getSize() != 0) {
								
								if (leastResistance.getDirection() == 0 && m.movement.getXComp() < 0) {
									m.movement = m.movement.negateXComp();
								}
								if (leastResistance.getDirection() == 180 && m.movement.getXComp() > 0) {
									m.movement = m.movement.negateXComp();
								}
								if (leastResistance.getDirection() == 270 && m.movement.getYComp() > 0) {
									m.movement = m.movement.negateYComp();
									m.inAir = false;
								}
								if (leastResistance.getDirection() == 90 && m.movement.getYComp() < 0) {
									m.movement = m.movement.negateYComp();
								}
								
								m.x+=leastResistance.getXComp();
								m.y+=leastResistance.getYComp();
							}
						}
					}
				}
			}
		}
	}
	
	public Block[] getUnorderedBlocks() {
		Block[] result = new Block[blocks.length*blocks[0].length];
		for (int y = 0; y < blocks.length; y++) {
			for (int x = 0; x < blocks[y].length; x++) {
				result[y*blocks[y].length+x] = blocks[y][x];
			}
		}
		return result;
	}
	public ArrayList<Block> getSolidBlocks() {
		ArrayList<Block> result = new ArrayList<Block>();
		for (int y = 0; y < blocks.length; y++) {
			for (int x = 0; x < blocks[y].length; x++) {
				if (blocks[y][x].solid) {
					result.add(blocks[y][x]);
				}
			}
		}
		return result;
	}
	public void draw(Graphics2D g2) {
		for (int y = 0; y < blocks.length; y++) {
			for (int x = 0; x < blocks[y].length; x++) {
				blocks[y][x].draw(g2);
			}
		}
	}
}
