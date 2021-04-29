package tdtest.shaders;

import tdlib.shaders.Shader;
import tdlib.utils.IOUtils;

import static org.lwjgl.opengl.GL46.*;

public class TextureShader extends Shader {
	public TextureShader() {
		super(IOUtils.readFile("res/shaders/TextureShader.vs"), IOUtils.readFile("res/shaders/TextureShader.fs"));
	}

	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "texCoords");
	}
	
	public void draw(int vbo, int vertexCount) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 5*4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 5*4, 3*4);
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
