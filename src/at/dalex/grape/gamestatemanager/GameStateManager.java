package at.dalex.grape.gamestatemanager;

import java.util.HashMap;
import java.util.UUID;

import org.joml.Matrix4f;

public class GameStateManager {

	private HashMap<UUID, GameState> gameStates = new HashMap<>();
	private UUID currentState;

	public void draw(Matrix4f projectionAndViewMatrix) {
		if (currentState != null) {
			gameStates.get(currentState).draw(projectionAndViewMatrix);
		}
	}

	public void update(double delta) {
		if (currentState != null) {
			gameStates.get(currentState).update(delta);
		}
	}

	public void addGameState(GameState gameState) {
		gameStates.put(gameState.getId(), gameState);
	}

	public void removeGameState(GameState gameState) {
		removeGameState(gameState.getId());
	}

	public void removeGameState(UUID gameStateId) {
		if (gameStates.containsKey(gameStateId)) {
			gameStates.remove(gameStateId);
		}
	}

	public void setState(UUID gameStateId) {
		if (gameStates.containsKey(gameStateId)) {
			gameStates.get(gameStateId).init();
			currentState = gameStateId;
		}
	}

	public UUID getCurrentState() {
		return this.currentState;
	}

	public UUID genGameStateId() {
		boolean done = false;
		UUID id = null;
		while (!done) {
			id = UUID.randomUUID();
			if (!gameStates.containsKey(id)) done = true;
		}
		return id;
	}
}
