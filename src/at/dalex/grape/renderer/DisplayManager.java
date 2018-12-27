package at.dalex.grape.renderer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.UUID;

import at.dalex.grape.developer.GameInfo;
import at.dalex.grape.toolbox.Type;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.input.KeyListener;
import at.dalex.grape.input.MouseClickListener;
import at.dalex.grape.input.MouseListener;
import at.dalex.grape.input.Scroll;


public class DisplayManager {

	public static int windowWidth = 720;
	public static int windowHeight = 480;
	private String windowTitle = "Grape Engine";
	private boolean vSync = false;
	private long windowHandle;
	
	private Timer timer;

	private DisplayInterface handler;

	public DisplayManager(String title, DisplayInterface windowHandler) {
		this.windowTitle = title;
		this.handler = windowHandler;

		//Parse width and height which is set in GameInfo.txt
		GameInfo gameInfo = GrapeEngine.getEngine().getGameInfo();
		if (gameInfo.getValue("window_override").equals("true")) {
			windowWidth = Type.parseInt(gameInfo.getValue("window_width"));
			windowHeight = Type.parseInt(gameInfo.getValue("window_height"));
		}

		//Change resolution to display's, when fullscreen is set in GameInfo.txt
		if (GrapeEngine.getEngine().getGameInfo().getValue("fullscreen").equalsIgnoreCase("true")) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			windowWidth = (int) screenSize.getWidth();
			windowHeight = (int) screenSize.getHeight();
		}

		//Change vSync if set in GameInfo.txt
		vSync = Boolean.valueOf(GrapeEngine.getEngine().getGameInfo().getValue("use_vsync")) || vSync;

		timer = new Timer();
	}

	public void destroy() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(windowHandle);
		glfwDestroyWindow(windowHandle);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	public void createDisplay() {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		//GL Version selection
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

		//Create the window
		windowHandle = glfwCreateWindow(windowWidth, windowHeight, windowTitle + " Snapshot", NULL, NULL);
		if (windowHandle == NULL) {
			throw new RuntimeException("Failed to create the GLFW window.");
		}

		// Make the OpenGL context current
		glfwMakeContextCurrent(windowHandle);

		//Initialize OpenGL functions
		GL.createCapabilities();

		//Setup callbacks
		glfwSetKeyCallback(windowHandle, new KeyListener());
		glfwSetCursorPosCallback(windowHandle, new MouseListener());
		glfwSetMouseButtonCallback(windowHandle, new MouseClickListener());
		glfwSetScrollCallback(windowHandle, new Scroll());

		glfwSetWindowCloseCallback(windowHandle, new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long arg0) {
				GrapeEngine.getEngine().onDisable();
				System.exit(0);
			}
		});

		//Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); //int*
			IntBuffer pHeight = stack.mallocInt(1); //int*

			glfwGetWindowSize(windowHandle, pWidth, pHeight);

			GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			//Center the window
			glfwSetWindowPos(
					windowHandle,
					(vidMode.width() - pWidth.get(0)) / 2,
					(vidMode.height() - pHeight.get(0)) / 2
			);
		}

		// Enable v-sync
		if (vSync)
			glfwSwapInterval(1);
		else glfwSwapInterval(0);

		// Make the window visible
		glfwShowWindow(windowHandle);

		// Set the clear color
		glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
	}

	public void loop() {
		while (!glfwWindowShouldClose(windowHandle)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			timer.update();
			timer.updateUPS();
			timer.updateFPS();
			handler.updateEngine(timer.getDelta());

			// swap the color buffers
			glfwSwapBuffers(windowHandle);

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public void enableVsync(boolean vsync) {
		this.vSync = vsync;
	}

	//Package private method
	long getWindowHandle() {
		return this.windowHandle;
	}

	public Timer getTimer() {
		return this.timer;
	}
}
