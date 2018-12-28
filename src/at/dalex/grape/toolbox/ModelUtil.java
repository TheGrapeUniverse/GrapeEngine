package at.dalex.grape.toolbox;

import at.dalex.grape.graphics.mesh.Model;

public class ModelUtil {

	public Model createRectangle(int x, int y, int w, int h) {
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

		return new Model(vertices, indices, 3);
	}
}
