package tdtest.map;

import static org.lwjgl.opengl.GL46.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import tdlib.collision.Collision;
import tdlib.objects.ColoredVertex;
import tdlib.utils.ColorUtils;
import tdlib.utils.IOUtils;
import tdlib.utils.ShapeUtils;
import tdtest.entities.Light;
import tdtest.entities.Player;

public class Room {
	private Integer[][] blocks;
	public Light[] lights = new Light[10];
	public int roomWidth, roomHeight;
	public int vbo;
	public final Vector2i roomPos;
	
	public Vector2i[] exits = new Vector2i[4];
	public Room(Vector2i roomPos, String path) {
		this.roomPos = roomPos;
		vbo = glGenBuffers();
		
		String fileData = IOUtils.readFile(path);
		if (fileData == null || fileData.isEmpty()) {
			fileData = IOUtils.readFile("res/newmaps/Blank");
		}
		String[][] layout = IOUtils.parseGrid(IOUtils.getProperty("Layout", fileData));
		
		roomWidth = layout[0].length; 
		roomHeight = layout.length;
		blocks = new Integer[roomHeight][roomWidth];
		
		float halfBlockWidth = 1f/roomWidth, halfBlockHeight = 1f/roomHeight;
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(roomWidth*roomHeight*6*6);
		for (int y = 0; y < roomHeight; y++) {
			for (int x = 0; x < roomWidth; x++) {
				blocks[y][x] = Integer.valueOf(layout[y][x]);
				Vector3f color = getBlockColor(blocks[y][x]);
				ColoredVertex[] vertices = new ColoredVertex[] {
						new ColoredVertex(x*halfBlockWidth*2-1+halfBlockWidth-halfBlockWidth, -y*halfBlockHeight*2+1-halfBlockHeight-halfBlockHeight, 0, color),
						new ColoredVertex(x*halfBlockWidth*2-1+halfBlockWidth+halfBlockWidth, -y*halfBlockHeight*2+1-halfBlockHeight-halfBlockHeight, 0, color),
						new ColoredVertex(x*halfBlockWidth*2-1+halfBlockWidth+halfBlockWidth, -y*halfBlockHeight*2+1-halfBlockHeight+halfBlockHeight, 0, color),
						new ColoredVertex(x*halfBlockWidth*2-1+halfBlockWidth-halfBlockWidth, -y*halfBlockHeight*2+1-halfBlockHeight+halfBlockHeight, 0, color),
				};
				ShapeUtils.getColoredShape(vertices, ShapeUtils.square, buffer);
			}
		}
		buffer.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		String lightsData = IOUtils.getProperty("Lights", fileData);
		if (lightsData != null) {
			for (int i = 0; i < 10; i++) {
				String lightData = IOUtils.getProperty(String.valueOf(i), lightsData, ':');
				if (lightData != null) {
					String[] splitData = lightData.split(",");
					lights[i] = new Light(
							new Vector2f(Float.valueOf(splitData[0]), Float.valueOf(splitData[1])),
							Float.valueOf(splitData[2]), Float.valueOf(splitData[3]),
							Float.valueOf(splitData[4]), Float.valueOf(splitData[5])
							);
				}
			}
		}
		
		exits[0] = new Vector2i(roomPos.x, roomPos.y+1);
		exits[1] = new Vector2i(roomPos.x+1, roomPos.y);
		exits[2] = new Vector2i(roomPos.x, roomPos.y-1);
		exits[3] = new Vector2i(roomPos.x-1, roomPos.y);
		String exitData = IOUtils.getProperty("Exits", fileData);
		if (exitData != null) {
			String uExit = IOUtils.getProperty("Up", exitData, ':');
			String rExit = IOUtils.getProperty("Right", exitData, ':');
			String dExit = IOUtils.getProperty("Down", exitData, ':');
			String lExit = IOUtils.getProperty("Left", exitData, ':');
			if (uExit != null) {
				String[] splitUExit = uExit.split(",");
				exits[0].set(Integer.valueOf(splitUExit[1]), Integer.valueOf(splitUExit[0]));
			}
			if (rExit != null) {
				String[] splitRExit = rExit.split(",");
				exits[1].set(Integer.valueOf(splitRExit[1]), Integer.valueOf(splitRExit[0]));
			}
			if (dExit != null) {
				String[] splitLExit = dExit.split(",");
				exits[2].set(Integer.valueOf(splitLExit[1]), Integer.valueOf(splitLExit[0]));
			}
			if (lExit != null) {
				String[] splitDExit = lExit.split(",");
				exits[3].set(Integer.valueOf(splitDExit[1]), Integer.valueOf(splitDExit[0]));
			}
		}
	}
	
	public static Vector3f getBlockColor(Integer type) {
		if (type == null) {
			return ColorUtils.BROWN;
		}
		int aType = Math.abs(type);
		if (aType == 1) {
			return ColorUtils.WHITE;
		} 
		else if (aType == 2) {
			return ColorUtils.BLACK;
		} 
		else if (aType == 3) {
			return ColorUtils.RED;
		} 
		else if (aType == 4) {
			return ColorUtils.GREEN;
		} 
		else if (aType == 5) {
			return ColorUtils.BLUE;
		} 
		else {
			return ColorUtils.YELLOW;
		}
	}
	
	private boolean isSolid(int x, int y) {
		if (x < 0 || y < 0 || x >= roomWidth || y >= roomHeight) {
			return false;
		} else {
			return blocks[y][x] > 0;
		}
	}
	
	/**
	 * Request collision on a player
	 * @param p
	 * @return All the directions the player got pushed in this collisioncheck
	 */
	public boolean[] requestCollision(Player p) {
		boolean[] directionsPushed = new boolean[4];
		float halfBlockWidth = 1f/roomWidth, halfBlockHeight = 1f/roomHeight;
		for (int y = 0; y < blocks.length; y++) {
			for (int x = 0; x < blocks[y].length; x++) {
				if (isSolid(x, y)) {
					ArrayList<Vector2f> collisionVectors = Collision.getCollisionVectors(
							p.position, 
							p.size, 
							new Vector2f(x*halfBlockWidth*2-1+halfBlockWidth-halfBlockWidth, -y*halfBlockHeight*2+1-halfBlockHeight-halfBlockHeight), 
							new Vector2f(halfBlockWidth*2, halfBlockHeight*2)
					);
					if (collisionVectors != null) {
						Vector2f collisionVector = null;
						for (int i = 0; i < 4; i++) {
							collisionVector = collisionVectors.get(i);
							if (	collisionVector.y > 0 && !isSolid(x, y-1) ||
									collisionVector.y < 0 && !isSolid(x, y+1) ||
									collisionVector.x > 0 && !isSolid(x+1, y) ||
									collisionVector.x < 0 && !isSolid(x-1, y) ||
									(collisionVector.x == 0 && collisionVector.y == 0)) {
								break;
							}
						}
						p.position.add(collisionVector);
						if (collisionVector.y > 0) {
							directionsPushed[Collision.UP] = true;
						} else if (collisionVector.y < 0) {
							directionsPushed[Collision.DOWN] = true;
						} else if (collisionVector.x > 0) {
							directionsPushed[Collision.RIGHT] = true;
						} else if (collisionVector.x < 0) {
							directionsPushed[Collision.LEFT] = true;
						}
					}
				}
			}
		}
		return directionsPushed;
	}
}
