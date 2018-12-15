package at.dalex.grape.renderer.mesh;

import at.dalex.grape.renderer.VertexArrayObject;

public class RawModel {

	private int vaoID;
	private int vertexCount;
	
	/**
	 * Creates a new RawModel object, which contains information about it's {@link VertexArrayObject}
	 * and it's vertex count.
	 * @param vaoID
	 * @param vertexCount
	 */
	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	
	/**
	 * Returns the {@link VertexArrayObject} ID of this <code>RawModel</code>
	 * @return
	 */
	public int getVaoID() {
		return this.vaoID;
	}
	
	/**
	 * Returns the amount of vertices
	 * @return Amount of vertices
	 */
	public int getVertexCount() {
		return this.vertexCount;
	}
}