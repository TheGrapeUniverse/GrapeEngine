package at.dalex.grape.toolbox;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Toolbox {

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		return createTransformationMatrix(translation, rx, ry, rz, scale, scale, scale);
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scaleX, float scaleY, float scaleZ) {
		Matrix4f matrix = createTransformationMatrixWithoutScaling(translation, rx, ry, rz);
		/* Scale */
		matrix.scale(scaleX, scaleY, scaleZ);
		return matrix;
	}

	private static Matrix4f createTransformationMatrixWithoutScaling(Vector3f trans, float rx, float ry, float rz) {
		Matrix4f matrix = new Matrix4f();
		/* Reset the matrix */
		matrix.identity();

		/* Translate */
		matrix.translate(trans);

		/* Rotate */
		matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0));
		matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0));
		matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1));
		return matrix;
	}
}
