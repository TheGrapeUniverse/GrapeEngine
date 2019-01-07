package at.dalex.grape.map;

import java.util.ArrayList;
import java.util.List;

import at.dalex.grape.graphics.BatchRenderer;
import org.joml.Matrix4f;

public class Map {

	private List<MapLayer> layers = new ArrayList<>();
	private int width;
	private int height;
	
	private int x;
	private int y;
	
	private float scale_factor = 1f;

	private BatchRenderer tileRenderer = new BatchRenderer();
	
	public Map(int width, int height, int layerAmount) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void addLayer(MapLayer layer) {
		layers.add(layer);
	}
	
	public MapLayer getLayer(int zIndex) {
		return layers.get(zIndex);
	}
	
	public void scale(float scaleFactor) {
		scale_factor += scaleFactor;
	}
	
	public void setScale(float scale) {
		scale_factor = scale;
	}
	
	public float getScale() {
		return this.scale_factor;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void update() {
		
	}
	
	public void draw(Matrix4f projectionAndViewMatrix) {
		for (MapLayer layer : layers) {
			layer.draw(tileRenderer, x, y, scale_factor, projectionAndViewMatrix);
		}

		tileRenderer.drawQueue(projectionAndViewMatrix);
	}
}
