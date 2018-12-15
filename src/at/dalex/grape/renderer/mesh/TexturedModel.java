package at.dalex.grape.renderer.mesh;

import at.dalex.grape.renderer.graphicsutil.Graphics;

public class TexturedModel {

	private RawModel rawModel;
	private int textureId;
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
	
	public float[] getUVCoordinates() {
		return this.uvs;
	}
	
	public static TexturedModel create(float[] vertices, int[] indices, float[] textureCoordinates, int textureId) {
		RawModel rawModel = Graphics.getLoader().loadToVAO(vertices, textureCoordinates, indices);
		return new TexturedModel(rawModel, textureId);
	}
}
