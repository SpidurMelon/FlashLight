package tdtest.main;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

import tdlib.input.InputMaster;
import tdlib.utils.IOUtils;
import tdlib.utils.PrintUtils;
import tdtest.entities.Light;
import tdtest.entities.Player;
import tdtest.map.Floor;
import tdtest.map.Room;
import tdtest.shaders.LightningShader;

public class World {
	private LightningShader shader;
	private static Player player;
	
	public static int floorNumber = 0;
	public static String[] floors;
	public static Floor currentFloor;
	
	private int ticksThisSecond, totalTickTime;
	private int drawsThisSecond, totalDrawTime;
	
	public static boolean DEBUG = false;
	//TODO Mechanic?: Going right is evil, going left is good.
	public World() {
		//TextureMaster.init();
		InputMaster.init(FlashLight.frame.windowID);
		
		shader = new LightningShader();
		shader.bind();
		shader.setUniform("screenSize", new Vector2f(FlashLight.frame.width, FlashLight.frame.height));
		
		player = new Player(new Vector2f(2f/40f, 2f/20f));
		
		floors = IOUtils.readFile("res/newmaps/WorldsConfig").split(",");
		currentFloor = new Floor("res/newmaps/" + floors[floorNumber]);
		
		if (DEBUG) {
			secondTimer.start();
		}
	}
	public void tick(double delta) {
		long startTick = System.nanoTime();
		
		player.tick((float)delta);
		if (player.position.x > 1) {
			player.position.x -= 2;
			currentFloor.moveRoom(1);
		}
		if (player.position.x < -1) {
			player.position.x += 2;
			currentFloor.moveRoom(3);
		}
		if (player.position.y > 1) {
			player.position.y -= 2;
			currentFloor.moveRoom(0);
		}
		if (player.position.y < -1) {
			player.position.y += 2;
			currentFloor.moveRoom(2);
		}
		
		Light[] allLights = new Light[12];
		allLights[0] = player.flashLight;
		allLights[1] = player.aura;
		for (int i = 0; i < currentFloor.getCurrentRoom().lights.length; i++) {
			allLights[i+2] = currentFloor.getCurrentRoom().lights[i];
		}
		shader.setLights(allLights);
		
		if (InputMaster.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			secondTimer.stop();
			GLFW.glfwSetWindowShouldClose(FlashLight.frame.windowID, true);
		}
		
		totalTickTime += System.nanoTime()-startTick;
		ticksThisSecond++;
	}
	
	private Timer secondTimer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("TPS: " + ticksThisSecond + ", FPS: " + drawsThisSecond);
			System.out.println("Avg. Tick time: " + totalTickTime/ticksThisSecond + ", Avg. Draw time: " + totalDrawTime/drawsThisSecond);
			System.out.println("Total memory: " + Runtime.getRuntime().totalMemory()/(1024*1024));
			System.out.println("Free memory: " + Runtime.getRuntime().freeMemory()/(1024*1024));
			ticksThisSecond = 0;
			drawsThisSecond = 0;
			totalTickTime = 0;
			totalDrawTime = 0;
		}
	});
	public void draw() {
		long startDraw = System.nanoTime();
		
		shader.draw(currentFloor.getCurrentRoom().vbo, currentFloor.getCurrentRoom().roomWidth*currentFloor.getCurrentRoom().roomHeight*6);
		shader.draw(player.vbo, 6);
		
		totalDrawTime += System.nanoTime()-startDraw;
		drawsThisSecond++;
	}
	public void destroy() {
		glDeleteBuffers(player.vbo);
		currentFloor.destroy();
	}
}
