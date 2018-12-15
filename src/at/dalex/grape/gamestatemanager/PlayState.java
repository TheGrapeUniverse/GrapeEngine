package at.dalex.grape.gamestatemanager;

import java.util.ArrayList;

import org.joml.Matrix4f;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.entity.Entity;
import at.dalex.grape.map.Map;
import at.dalex.grape.renderer.graphicsutil.Graphics;
import at.dalex.grape.script.LuaManager;

public class PlayState extends GameState {
	
	LuaManager luaManager;
	public static Map current_map;
	public static ArrayList<Entity> entities = new ArrayList<>();
	
	@Override
	public void init() {
		luaManager = GrapeEngine.getEngine().getLuaManager();
		luaManager.executeMain();
		luaManager.callInit();
		
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
