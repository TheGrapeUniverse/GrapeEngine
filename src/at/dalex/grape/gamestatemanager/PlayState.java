package at.dalex.grape.gamestatemanager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import at.dalex.grape.graphics.BatchRenderer;
import at.dalex.grape.graphics.TilemapRenderer;
import at.dalex.grape.graphics.Tileset;
import at.dalex.grape.graphics.graphicsutil.Image;
import at.dalex.grape.graphics.graphicsutil.ImageUtils;
import at.dalex.grape.graphics.graphicsutil.TextureAtlas;
import at.dalex.grape.map.MapGenerator;
import org.joml.Matrix4f;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.entity.Entity;
import at.dalex.grape.map.Map;
import at.dalex.grape.graphics.graphicsutil.Graphics;
import at.dalex.grape.script.LuaManager;

public class PlayState extends GameState {
	
	private LuaManager luaManager;
	public static Map current_map;
	private TilemapRenderer tilemapRenderer;
	public static ArrayList<Entity> entities = new ArrayList<>();

	@Override
	public void init() {
		luaManager = GrapeEngine.getEngine().getLuaManager();
		luaManager.executeMain();
		luaManager.callInit();
		current_map = MapGenerator.generateFromPerlinNoise(128, 128, 2);
		current_map.setScale(0.5f);
		BufferedImage atlas = ImageUtils.loadBufferedImage("textures/base.png");
		this.tilemapRenderer = new TilemapRenderer(current_map, new Tileset(atlas, 16));
		this.tilemapRenderer.preCacheRender();
	}

	@Override
	public void draw(Matrix4f projectionAndViewMatrix) {
		Graphics.enableBlending(true);
		if (tilemapRenderer != null)
			tilemapRenderer.draw(projectionAndViewMatrix);

		luaManager.callDraw();

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
