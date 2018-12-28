package at.dalex.grape.graphics.mesh;

import at.dalex.grape.graphics.graphicsutil.Graphics;

public class TexturedModel {

	private RawModel rawModel;
	private int textureId;

	private int numberOfRows = 1;

	private float[] uvs;

	public TexturedModel(RawModel rawModel, int textureId) {
		this.rawModel = rawModel;
		this.textureId = textureId;
	}

	public RawModel getRawModel() {
		return this.rawModel;
	}

	public int getTextureId() {
		return this.textureId;
	}

	public int getNumberOfRows() {
		return this.numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public float[] getUVCoordinates() {
		return this.uvs;
	}
	
	public static TexturedModel create(float[] vertices, int[] indices, float[] textureCoordinates, int textureId) {
		RawModel rawModel = Graphics.getLoader().loadToVAO(vertices, textureCoordinates, indices);
		return new TexturedModel(rawModel, textureId);
	}
}
