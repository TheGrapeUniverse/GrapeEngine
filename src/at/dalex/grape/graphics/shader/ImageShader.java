package at.dalex.grape.graphics.shader;

import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import at.dalex.grape.graphics.mesh.TexturedModel;
import at.dalex.grape.resource.FileContentReader;
import at.dalex.grape.toolbox.MemoryManager;

public class ImageShader extends ShaderProgram {

	private int position_projectionMatrix;
	
	public ImageShader() {
		super(FileContentReader.readFile("/shaders/ImageShader.vsh"), FileContentReader.readFile("/shaders/ImageShader.fsh"));
	}
	
	@Override
	public void getAllUniformLocations() {
		position_projectionMatrix = getUniformLoader().getUniformLocation("projectionMatrix");
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
	}

	public void drawMesh(TexturedModel model, Matrix4f projectionAndViewMatrix) {
		start();
		GL30.glBindVertexArray(model.getRawModel().getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		getUniformLoader().loadMatrix(position_projectionMatrix, projectionAndViewMatrix);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTextureId());
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		MemoryManager.verticesAmount += model.getRawModel().getVertexCount();
		MemoryManager.drawCallsAmount++;
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		stop();
	}
}
