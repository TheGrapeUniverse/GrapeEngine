package at.dalex.grape.gamestatemanager;

import java.awt.*;
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
import at.dalex.grape.map.chunk.OpenSimplexNoise;
import org.joml.Matrix4f;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.entity.Entity;
import at.dalex.grape.map.Map;
import at.dalex.grape.graphics.graphicsutil.Graphics;
import at.dalex.grape.script.LuaManager;
import org.joml.Vector3f;

public class PlayState extends GameState {
	
	private LuaManager luaManager;
	public static Map current_map;
	public static ArrayList<Entity> entities = new ArrayList<>();

	private ChunkWorld world;
	private ChunkWorldRenderer chunkWorldRenderer;
	private TilemapRenderer renderer;

	@Override
	public void init() {
		luaManager = GrapeEngine.getEngine().getLuaManager();
		luaManager.executeMain();
		luaManager.callInit();
		current_map = MapGenerator.generateFromPerlinNoise(256, 256, new Random().nextInt());
		current_map.setScale(0.125f);

		this.world = new ChunkWorld(2);
		BufferedImage atlas = ImageUtils.loadBufferedImage("textures/base.png");
		renderer = new TilemapRenderer(current_map, new Tileset(atlas, 16));
		renderer.preCacheRender();

		world.generateChunkAt(0, 0);

		chunkWorldRenderer = new ChunkWorldRenderer(new Tileset(atlas, 16), world);
	}

	@Override
	public void draw(Matrix4f projectionAndViewMatrix) {
		Graphics.enableBlending(true);

		luaManager.callDraw();

		Entity p = entities.get(0);
		if (p != null) {
			chunkWorldRenderer.cacheChunksInRange((int) (p.getX() / 16) / 16, (int) (p.getY() / 16) / 16);
		}

		chunkWorldRenderer.drawChunkQueue(GrapeEngine.getEngine().getCamera().getProjectionAndViewMatrix());

		for (Entity entity : entities) {
			entity.draw(GrapeEngine.getEngine().getCamera().getProjectionMatrix());
		}

		if (p != null)
			Graphics.fillRectangle((int) p.getX(), (int) p.getY(), 32, 32, Color.RED, GrapeEngine.getEngine().getCamera().getProjectionMatrix());

		Graphics.enableBlending(false);
	}

	@Override
	public void update(double delta) {
		if (current_map != null)
			current_map.update();

		Entity p = entities.get(0);
		if (p != null) {
			GrapeEngine.getEngine().getCamera().setPosition(new Vector3f((float) -p.getX(), (float) -p.getY(), 0.0f));
			System.out.println("pX: " + p.getX());
		}

		luaManager.callUpdate();

		for (Entity entity : entities) {
			entity.update(delta);
		}
	}
}
