package tdtest.shaders;

import tdlib.shaders.Shader;
import tdlib.utils.IOUtils;
import tdtest.entities.Light;

import static org.lwjgl.opengl.GL46.*;

import java.nio.FloatBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

public class LightningShader extends Shader {
	public LightningShader() {
		super(IOUtils.readFile("res/shaders/LightningShader.vs"), IOUtils.readFile("res/shaders/LightningShader.fs"));
		
		addUniform("lightsPos");
		addUniform("lightsData");
	}
	
	private static Light inActiveLight = new Light(new Vector2f(), 0, 0, 0, 0);
	public void setLights(Light... lights) {
		FloatBuffer positions = BufferUtils.createFloatBuffer(lights.length*2);
		FloatBuffer data = BufferUtils.createFloatBuffer(lights.length*4);
		for (Light l:lights) {
			if (l != null) {
				l.getPosition(positions);
				l.getData(data);
			} else {
				inActiveLight.getPosition(positions);
				inActiveLight.getData(data);
			}
		}
		positions.flip();
		data.flip();
		glUniform2fv(uniformPointers.get("lightsPos"), positions);
		glUniform4fv(uniformPointers.get("lightsData"), data);
	}

	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "color");
	}
	
	public void draw(int vbo, int vertexCount) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 6*4, 0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 6*4, 3*4);
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
