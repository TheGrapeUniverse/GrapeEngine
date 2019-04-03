package at.dalex.grape.gamestatemanager;

import java.util.ArrayList;

import at.dalex.grape.graphics.font.BitmapFont;
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
	public static ArrayList<Entity> entities = new ArrayList<>();
	private BitmapFont font;

	@Override
	public void init() {
		luaManager = GrapeEngine.getEngine().getLuaManager();
		luaManager.executeMain();
		luaManager.callInit();
		current_map = MapGenerator.generateFromPerlinNoise(128, 64, 12394);
		current_map.setScale(0.50f);
		current_map.prepareRender(GrapeEngine.getEngine().getCamera().getProjectionMatrix());
		font = new BitmapFont();
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
