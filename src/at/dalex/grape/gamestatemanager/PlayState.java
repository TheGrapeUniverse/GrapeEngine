package at.dalex.grape.gamestatemanager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import at.dalex.grape.graphics.BatchRenderer;
import at.dalex.grape.graphics.ChunkWorldRenderer;
import at.dalex.grape.graphics.TilemapRenderer;
import at.dalex.grape.graphics.Tileset;
import at.dalex.grape.graphics.graphicsutil.Image;
import at.dalex.grape.graphics.graphicsutil.ImageUtils;
import at.dalex.grape.graphics.graphicsutil.TextureAtlas;
import at.dalex.grape.map.MapGenerator;
import at.dalex.grape.map.chunk.ChunkWorld;
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

	private ChunkWorld world;
	private ChunkWorldRenderer chunkWorldRenderer;

	@Override
	public void init() {
		luaManager = GrapeEngine.getEngine().getLuaManager();
		luaManager.executeMain();
		luaManager.callInit();
		current_map = MapGenerator.generateFromPerlinNoise(16, 16, new Random().nextInt());
		current_map.setScale(0.5f);

		this.world = new ChunkWorld(1);
		BufferedImage atlas = ImageUtils.loadBufferedImage("textures/debug.png");

		world.generateChunkAt(0, 0);
		world.generateChunkAt(0, 1);
		world.generateChunkAt(1, 0);

		chunkWorldRenderer = new ChunkWorldRenderer(new Tileset(atlas, 16), world);
	}

	@Override
	public void draw(Matrix4f projectionAndViewMatrix) {
		Graphics.enableBlending(true);

		chunkWorldRenderer.drawChunkAt(0, 0, projectionAndViewMatrix);
		chunkWorldRenderer.drawChunkAt(0, 1, projectionAndViewMatrix);
		chunkWorldRenderer.drawChunkAt(1, 0, projectionAndViewMatrix);

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
