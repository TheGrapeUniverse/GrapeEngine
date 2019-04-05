package at.dalex.grape.toolbox;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
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

	private static Matrix4f createTransformationMatrixWithoutScaling(Vector3f translation, float rx, float ry, float rz) {
		Matrix4f matrix = new Matrix4f();
		/* Reset the matrix */
		matrix.identity();

		/* Translate */
		matrix.translate(translation);

		/* Rotate */
		matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0));
		matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0));
		matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1));
		return matrix;
	}

	private static Matrix3f createUVTransformationMatrix(Vector2f translation, Vector2f scale, float rx, float ry) {
		Matrix3f matrix = new Matrix3f();
		matrix.identity();
		matrix.transform()
		matrix.scale(translation.x, translation.y, 1.0f);

	}

	public static float[] convertMatrixToArray(Matrix4f matrix) {
		float[] data = new float[16];
		data[0] = matrix.m00();
		data[1] = matrix.m01();
		data[2] = matrix.m02();
		data[3] = matrix.m03();
		data[4] = matrix.m10();
		data[5] = matrix.m11();
		data[6] = matrix.m12();
		data[7] = matrix.m13();
		data[8] = matrix.m20();
		data[9] = matrix.m21();
		data[10] = matrix.m22();
		data[11] = matrix.m23();
		data[12] = matrix.m30();
		data[13] = matrix.m31();
		data[14] = matrix.m32();
		data[15] = matrix.m33();
		return data;
	}
}
