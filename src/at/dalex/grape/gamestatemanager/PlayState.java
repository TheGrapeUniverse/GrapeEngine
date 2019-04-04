package at.dalex.grape.gamestatemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import at.dalex.grape.graphics.BatchRenderer;
import at.dalex.grape.graphics.graphicsutil.Image;
import at.dalex.grape.graphics.graphicsutil.ImageUtils;
import at.dalex.grape.graphics.graphicsutil.TextureAtlas;
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
		TextureAtlas atlas = new TextureAtlas(image.getTextureId(), image.getWidth(), 2);
		renderer = new BatchRenderer(image.getTextureId());

		float[] uvs = new float[] { 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0 };
		uvs = atlas.recalculateUVCoordinates(uvs, 1);

		renderer.queueRender(80, 80, 128, 128, uvs);
	}

	@Override
	public void draw(Matrix4f projectionAndViewMatrix) {
		Graphics.enableBlending(true);
		if (current_map != null)
			//current_map.draw(projectionAndViewMatrix);

		//luaManager.callDraw();

		renderer.drawQueue(projectionAndViewMatrix);

		for (Entity entity : entities) {
			//entity.draw(projectionAndViewMatrix);
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
