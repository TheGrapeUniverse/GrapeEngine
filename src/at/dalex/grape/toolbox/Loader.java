package at.dalex.grape.toolbox;

import at.dalex.grape.renderer.VertexArrayObject;
import at.dalex.grape.renderer.mesh.RawModel;

public class Loader {

	private VertexArrayObject vao;

	public RawModel loadToVAO(float[] vertices, int[] indices) {
		vao = new VertexArrayObject();

		vao.bindVAO();
		vao.bindIndicesBuffer(indices);
		vao.storeDataInAttributeList(0, 3, vertices);
		vao.unbindVAO();

		return new RawModel(vao.getID(), indices.length);
	}

	public RawModel loadToVAO(float[] vertices, float[] textureCoordinates, float[] normals, int[] indices) {
		vao = new VertexArrayObject();

		vao.bindVAO();
		vao.bindIndicesBuffer(indices);
		vao.storeDataInAttributeList(0, 3, vertices);
		vao.storeDataInAttributeList(1, 2, textureCoordinates);
		vao.storeDataInAttributeList(2, 3, normals);
		vao.unbindVAO();

		return new RawModel(vao.getID(), indices.length);
	}

	public RawModel loadToVAO(float[] vertices, float[] textureCoordinates, int[] indices) {
		vao = new VertexArrayObject();

		vao.bindVAO();
		vao.bindIndicesBuffer(indices);
		vao.storeDataInAttributeList(0, 3, vertices);
		vao.storeDataInAttributeList(1, 2, textureCoordinates);
		vao.unbindVAO();

		return new RawModel(vao.getID(), indices.length);
	}

	public RawModel loadToVAO(float[] vertices, int coordinateSize) {
		vao = new VertexArrayObject();

		vao.bindVAO();
		vao.storeDataInAttributeList(0, coordinateSize, vertices);
		vao.unbindVAO();

		//Divide by n because we have n coordinates per vertex
		return new RawModel(vao.getID(), vertices.length / coordinateSize);
	}

	public RawModel fromRectangle(int x, int y, int w, int h) {
		int sh = 0;
		float[] vertices = new float[] {
				x, (sh - y), 0.0f,
				x, sh - (y + h), 0.0f,

				x + w, sh - (y + h), 0.0f,
				x + w, (sh - y), 0.0f
		};

		int[] indices = new int[] {
				0, 1, 2, 0, 2, 3
		};

		vao = new VertexArrayObject();
		vao.bindVAO();
		vao.bindIndicesBuffer(indices);
		vao.storeDataInAttributeList(0, 3, vertices);
		vao.unbindVAO();

		return new RawModel(vao.getID(), indices.length);
	}

	public RawModel fromRectangle(int x, int y, int w, int h, float[] textureCoordinates) {
		float[] vertices = new float[] {
				x, y, 0.0f,
				x, y + h, 0.0f,

				x + w, y + h, 0.0f,
				x + w, y, 0.0f
		};

		int[] indices = new int[] {
				0, 1, 2, 0, 2, 3
		};

		vao = new VertexArrayObject();
		vao.bindVAO();
		vao.bindIndicesBuffer(indices);
		vao.storeDataInAttributeList(0, 3, vertices);
		vao.storeDataInAttributeList(1, 2, textureCoordinates);
		vao.unbindVAO();

		return new RawModel(vao.getID(), indices.length);
	}
}
