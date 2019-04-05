package at.dalex.grape.gamestatemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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

	BatchRenderer renderer;

	@Override
	public void init() {
		luaManager = GrapeEngine.getEngine().getLuaManager();
		luaManager.executeMain();
		luaManager.callInit();
		//current_map = MapGenerator.generateFromPerlinNoise(256, 256, 2);
		//current_map.setScale(0.25f);
		Image image = ImageUtils.loadImage(new File("textures/debug.png"));
		renderer = new BatchRenderer(image.getTextureId());
		TextureAtlas atlas = new TextureAtlas(image.getTextureId(), image.getWidth(), image.getHeight(), 2, 2);

		float[] rectangleUVs = new float[] { 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0 };
		renderer.queueRender(0, 0, 64, 64, atlas.recalculateUVCoordinates(rectangleUVs, 1));

		float[]	rectangleUVs2 = new float[] { 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0 };
		System.out.println("IsEqual: " + Arrays.equals(rectangleUVs, rectangleUVs2));
		renderer.queueRender(128, 0, 64, 64, atlas.recalculateUVCoordinates(rectangleUVs2, 3));
	}

	@Override
	public void draw(Matrix4f projectionAndViewMatrix) {
		Graphics.enableBlending(true);
		if (current_map != null)
			//current_map.draw(projectionAndViewMatrix);

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
