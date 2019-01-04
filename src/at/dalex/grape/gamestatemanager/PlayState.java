package at.dalex.grape.gamestatemanager;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import at.dalex.grape.graphics.BatchRenderer;
import at.dalex.grape.graphics.font.GrapeFont;
import at.dalex.grape.graphics.graphicsutil.ImageUtils;
import org.joml.Matrix4f;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.entity.Entity;
import at.dalex.grape.map.Map;
import at.dalex.grape.graphics.graphicsutil.Graphics;
import at.dalex.grape.script.LuaManager;

public class PlayState extends GameState {
	
	private LuaManager luaManager;
	public static Map current_map;
	public static ArrayList<Entity> entities = new ArrayList<>();

	private BatchRenderer renderer;

	private GrapeFont font;

	@Override
	public void init() {
		luaManager = GrapeEngine.getEngine().getLuaManager();
		luaManager.executeMain();
		luaManager.callInit();
		font = new GrapeFont("C:\\Users\\Clemi\\Desktop\\OpenSans-Light.ttf", 12);
		renderer = new BatchRenderer();
		Random random = new Random();

		for (int i = 0; i < 20; i++) {
			renderer.queueRender(ImageUtils.loadImage(new File("textures/debug.png")), random.nextInt(500), random.nextInt(500), 64, 64);
		}
	}

	@Override
	public void draw(Matrix4f projectionAndViewMatrix) {
		Graphics.enableBlending(true);
		if (current_map != null)
			current_map.draw(projectionAndViewMatrix);
		
		luaManager.callDraw();
		
		for (Entity entity : entities) {
			entity.draw(projectionAndViewMatrix);
		}

		font.drawString("Grape-Engine", 0, 16, Color.WHITE, projectionAndViewMatrix);

		renderer.drawQueue(projectionAndViewMatrix);

		Graphics.enableBlending(false);
	}

	@Override
	public void update(double delta) {
		if (current_map != null)
			current_map.update();
		
		luaManager.callUpdate();
		
		for (Entity entity : entities) {
			entity.update(delta);
		}
	}
}
