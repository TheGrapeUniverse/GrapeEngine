package at.dalex.grape.gamestatemanager;

import java.util.UUID;

import org.joml.Matrix4f;

import at.dalex.grape.GrapeEngine;

public abstract class GameState {

	private UUID gameStateId;
	
	public GameState() {
		gameStateId = GrapeEngine.getEngine().getGameStateManager().genGameStateId();
	}
	
	public abstract void init();
	public abstract void draw(Matrix4f projectionAndViewMatrix);
	public abstract void update(double delta);
	
	public UUID getId() {
		return this.gameStateId;
	}
}
