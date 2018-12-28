package at.dalex.grape.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

	private Vector3f position;
	private Matrix4f projectionMatrix;
	
	public Camera(int width, int height) {
		position = new Vector3f(0, 0, 0);
		projectionMatrix = new Matrix4f().setOrtho2D(0, width / 2, (height / 2), 0);
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void translate(Vector3f position) {
		this.position.add(position);
	}
	
	public Vector3f getPosition() {
		return this.position;
	}
	
	public Matrix4f getProjectionAndViewMatrix() {
		Matrix4f transformed = new Matrix4f(projectionMatrix);
		Matrix4f transformationMatrix = new Matrix4f();
		transformationMatrix.translate(position);
		transformed.mul(transformationMatrix);
		return transformed;
	}
	
	public Matrix4f getProjectionMatrix() {
		return new Matrix4f(projectionMatrix);
	}
}
