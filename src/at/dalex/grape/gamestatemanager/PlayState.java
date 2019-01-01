package at.dalex.grape.gamestatemanager;

import java.awt.*;
import java.util.ArrayList;

import at.dalex.grape.graphics.font.GrapeFont;
import org.joml.Matrix4f;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.entity.Entity;
import at.dalex.grape.map.Map;
import at.dalex.grape.graphics.graphicsutil.Graphics;
import at.dalex.grape.script.LuaManager;
import org.lwjgl.opengl.GL11;

public class PlayState extends GameState {
	
	private LuaManager luaManager;
	public static Map current_map;
	public static ArrayList<Entity> entities = new ArrayList<>();

	private GrapeFont font;

	@Override
	public void init() {
		luaManager = GrapeEngine.getEngine().getLuaManager();
		luaManager.executeMain();
		luaManager.callInit();
		font = new GrapeFont("C:\\Users\\Clemi\\Desktop\\OpenSans-Light.ttf", 12);
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

		font.drawString("GrapeEngine Font-Rendering is working as intended!", 0, 16, Color.GREEN, projectionAndViewMatrix);

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
