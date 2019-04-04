package at.dalex.grape.gamestatemanager;

import java.io.File;
import java.util.ArrayList;

import at.dalex.grape.graphics.BatchRenderer;
import at.dalex.grape.graphics.graphicsutil.Image;
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

	@Override
	public void init() {
		luaManager = GrapeEngine.getEngine().getLuaManager();
		luaManager.executeMain();
		luaManager.callInit();
		Image image = ImageUtils.loadImage(new File("textures/base.png"));
		renderer = new BatchRenderer(image);
		renderer.queueRender(80, 80, 64, 64, 0.0f, 0.0f, 1.0f, 1.0f);
	}

	@Override
	public void draw(Matrix4f projectionAndViewMatrix) {
		Graphics.enableBlending(true);
		if (current_map != null)
			current_map.draw(projectionAndViewMatrix);

		luaManager.callDraw();
		renderer.drawQueue(projectionAndViewMatrix);

		for (Entity entity : entities) {
			entity.draw(projectionAndViewMatrix);
		}

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
