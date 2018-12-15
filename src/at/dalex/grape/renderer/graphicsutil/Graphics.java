package at.dalex.grape.renderer.graphicsutil;

import java.awt.Color;
import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import at.dalex.grape.renderer.mesh.RawModel;
import at.dalex.grape.renderer.mesh.TexturedModel;
import at.dalex.grape.renderer.shader.ImageShader;
import at.dalex.grape.renderer.shader.SolidColorShader;
import at.dalex.grape.toolbox.Loader;
import at.dalex.grape.toolbox.Toolbox;

public class Graphics {

	static {
		loader = new Loader();
	}
	
	private static Loader loader;
	private static SolidColorShader solidColorShader;
	private static ImageShader imageShader;
	
	private static float[] rectangleUvs = new float[] {
			0, 0,
			0, 1,
			1, 1,
			1, 0
	};
	
	private static float[] rectangleBaseVertices = new float[] {
		0, 0, 0,
		0, 1, 0,
		1, 1, 0,
		1, 0, 0
	};
	
	private static int[] rectangleBaseIndices = new int[] {
		0, 1, 2, 0, 2, 3	
	};
	
	private static RawModel defaultRectangleModel;

	public Graphics() {
		solidColorShader = new SolidColorShader();
		imageShader = new ImageShader();
		defaultRectangleModel = loader.loadToVAO(rectangleBaseVertices, rectangleUvs, rectangleBaseIndices);
	}
	
	/**
	 * Draws an {@link Image} at the given X Y Coordinates.
	 * Set the desired width and height using those parameters.
	 * 
	 * @param image The {@link Image} you want to be drawn
	 * @param x The target X-coordinate
	 * @param y The target Y-coordinate
	 * @param width The desired width
	 * @param height The desired height
 	 */
	public static void drawImage(Image image, int x, int y, int width, int height, Matrix4f projectionAndViewMatrix) {
		drawImage(image.getTextureId(), x, y, width, height, projectionAndViewMatrix);
	}
	
	/**
	 * Draws an {@link Image} at the given X Y Coordinates.
	 * Set the desired width and height using those parameters.
	 * 
	 * @param image The {@link Image} you want to be drawn
	 * @param x The target X-coordinate
	 * @param y The target Y-coordinate
	 * @param width The desired width
	 * @param height The desired height
 	 */
	public static void drawImage(int textureId, int x, int y, int width, int height, Matrix4f projectionAndViewMatrix) {
		Matrix4f matrices = transformMatrix(projectionAndViewMatrix, x, y, width, height, 0f);
		imageShader.drawMesh(new TexturedModel(defaultRectangleModel, textureId), matrices);
	}
	
	/**
	 * Draws a filled rectangle at the given X Y Coordinates.
	 * Set the desired width and height using those parameters.
	 * 
	 * @param x The target X-coordinate
	 * @param y The target Y-coordinate
	 * @param width The desired width
	 * @param height The desired height
	 * @param color The color you want the rectangle to have
	 */
	public static void fillRectangle(int x, int y, int width, int height, Color color, Matrix4f projectionAndViewMatrix) {
		Matrix4f matrices = transformMatrix(projectionAndViewMatrix, x, y, width, height, 0f);
		solidColorShader.drawMesh(defaultRectangleModel, matrices, color);
	}
	
	public static void enableBlending(boolean blending) {
		if (blending) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		} else GL11.glDisable(GL11.GL_BLEND);
	}
	
	private static Matrix4f transformMatrix(Matrix4f projectionAndViewMatrix, int x, int y, int width, int height, float rZ) {
		Matrix4f transformationMatrix = Toolbox.createTransformationMatrix(
				new Vector3f(x / 2, y / 2, 0), 0f, 0f, rZ, width / 2, height / 2, 1); 	//Create a transformation matrix
		Matrix4f transformedMatrix = new Matrix4f(projectionAndViewMatrix);				// Create a clone of the projectionAndViewMatrix because we don't want to overwrite the instance
		return transformedMatrix.mul(transformationMatrix);								// Multiply the projectionAndViewMatrix with the transformationMatrix;
	}	
	
	/**
	 * Get the {@link Loader} instance, which is used to store data in VBOs or in attribute lists.
	 */
	public static Loader getLoader() {
		return loader;
	}
}
