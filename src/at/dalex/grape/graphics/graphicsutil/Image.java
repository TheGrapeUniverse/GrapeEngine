package at.dalex.grape.graphics.graphicsutil;

import org.lwjgl.opengl.GL11;

public class Image {

	private int textureId;
	private final int glTarget = GL11.GL_TEXTURE_2D;
	private int width;
	private int height;
	
	/**
	 * Creates a new Image instance. 
	 * Contains the width, height and texture id of a OpenGL texture
	 */
	public Image(int textureId, int width, int height) {
		this.textureId = textureId;
		this.width = width;
		this.height = height;
	}

	/**
	 * Bind this texture to the OpenGL context
	 */
	public void bind() {
		GL11.glBindTexture(glTarget, textureId);
	}

	/**
	 * Release this texture from the OpenGL context
	 */
	public void unbind() {
		GL11.glBindTexture(glTarget, 0);
	}

	/**
	 * Get the OpenGL texture id of this image
	 * @return OpenGL texture id
	 */
	public int getTextureId() {
		return this.textureId;
	}
	
	/**
	 * Get the width of this image
	 * @return width of this image
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Get the height of this image
	 * @return height of this image
	 */
	public int getHeight() {
		return this.height;
	}
}
