package tdtest.map;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector2i;
import static org.lwjgl.opengl.GL46.*;

public class Floor {
	private String directory;
	private HashMap<Vector2i, Room> rooms = new HashMap<Vector2i, Room>();
	private Room currentRoom;
	public Floor(String directory) {
		this.directory = directory;
		switchRoom(new Vector2i(0,0));
	}
	public void switchRoom(Vector2i roomCoords) {
		currentRoom = getRoom(roomCoords);
		loadRoom(new Vector2i(roomCoords.x, roomCoords.y+1));
		loadRoom(new Vector2i(roomCoords.x+1, roomCoords.y));
		loadRoom(new Vector2i(roomCoords.x, roomCoords.y-1));
		loadRoom(new Vector2i(roomCoords.x-1, roomCoords.y));
	}
	public void moveRoom(int direction) {
		switchRoom(currentRoom.exits[direction]);
	}
	public Room getRoom(Vector2i roomCoords) {
		if (rooms.containsKey(roomCoords)) {
			return rooms.get(roomCoords);
		} else {
			loadRoom(roomCoords);
			return getRoom(roomCoords);
		}
	}
	public void loadRoom(Vector2i roomCoords) {
		rooms.put(roomCoords, new Room(roomCoords, directory + "/Layer " + roomCoords.y + "/L" + roomCoords.y + "R" + roomCoords.x));
	}
	public Room getCurrentRoom() {
		return currentRoom;
	}
	public void destroy() {
		for (Room room:rooms.values()) {
			glDeleteBuffers(room.vbo);
		}
	}
}
