package at.dalex.grape.renderer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {

	private double lastLoopTime;
	private float deltaTime;
	private float timeCount;
	private int fps;
	private int fpsCount;
	private int ups;
	private int upsCount;

	public void init() {
		lastLoopTime = getTime();
	}

	public double getTime() {
		return glfwGetTime();
	}

	public float getDelta() {
		return this.deltaTime;
	}

	public void updateFPS() {
		fpsCount++;
	}

	public void updateUPS() {
		upsCount++;
	}

	public void update() {
		double time = getTime();
		deltaTime = (float) (time - lastLoopTime);
		lastLoopTime = time;
		timeCount += deltaTime;

		if (timeCount > 1f) {
			fps = fpsCount;
			fpsCount = 0;

			ups = upsCount;
			upsCount = 0;

			timeCount -= 1f;
		}
	}

	public int getFPS() {
		return fps > 0 ? fps : fpsCount;
	}
	
	public int getUPS() {
		return ups > 0 ? ups : upsCount;
	}
	
	public double getLastLoopTime() {
		return lastLoopTime;
	}
}
