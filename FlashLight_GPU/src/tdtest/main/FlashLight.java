package tdtest.main;

import org.lwjgl.opengl.GL46;

import tdlib.setup.GLFrame;
import tdlib.utils.IOUtils;
import tdlib.utils.PrintUtils;

public class FlashLight {
	public static GLFrame frame;
	public static void main(String[] args) {
		frame = new GLFrame("FlashLight") {
			private World world;
			public void init() {
				world = new World();
				GL46.glClearColor(1, 1, 1, 1);
			}
			public void loop(double delta) {
				world.tick(delta);
				world.draw();
			}
			public void destroy() {
				world.destroy();
			}
		};
		frame.startLoop(300);
	}
}
