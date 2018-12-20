package at.dalex.grape.renderer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
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
		
		//Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

		//Change resolution to display's, when fullscreen is set in GameInfo.txt
		if (GrapeEngine.getEngine().getGameInfo().getValue("fullscreen").equalsIgnoreCase("true")) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			windowWidth = (int) screenSize.getWidth();
			windowHeight = (int) screenSize.getHeight();
		}

		//Create the window
		windowHandle = glfwCreateWindow(windowWidth, windowHeight, windowTitle + " Snapshot " + UUID.randomUUID(), NULL, NULL);
		if (windowHandle == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		//Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(windowHandle, new KeyListener());
		//Setup a cursor callback.
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

		// Make the OpenGL context current
		glfwMakeContextCurrent(windowHandle);

		//Change vSync if set in GameInfo.txt
		vSync = Boolean.valueOf(GrapeEngine.getEngine().getGameInfo().getValue("use_vsync")) || vSync;

		// Enable v-sync
		if (vSync)
			glfwSwapInterval(1);
		else glfwSwapInterval(0);

		// Make the window visible
		glfwShowWindow(windowHandle);
		
		initializeWindow();
	}

	private void initializeWindow() {
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		setupOpenGL();
	}
	
	public void loop() {
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(windowHandle)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			timer.update();
			timer.updateUPS();
			timer.updateFPS();
			handler.updateEngine(timer.getDelta());

			glfwSwapBuffers(windowHandle); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	private void setupOpenGL() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glViewport(0, 0, windowWidth, windowHeight);
		GL11.glOrtho(0, windowWidth, windowHeight, 0, -1, 0);
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
