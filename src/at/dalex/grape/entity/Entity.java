package at.dalex.grape.entity;

import java.awt.Rectangle;
import java.util.UUID;

public abstract class Entity implements ITickable {

	protected int x = 0;
	protected int y = 0;
	private Rectangle bounds;
	protected UUID entityId;
	
	public Entity(int x, int y) {
		this.x = x;
		this.y = y;
		this.bounds = new Rectangle(x, y, 0, 0);
		this.entityId = EntityManager.genEntityId();
	}
	
	public boolean intersects(Entity ent) {
		return bounds.intersects(ent.getBounds());
	}
	
	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public Rectangle getBounds() {
		return this.bounds;
	}
	
	public void setBounds(int x, int y, int width, int height) {
		bounds.setBounds(x, y, width, height);
	}
	
	public UUID getUniqueId() {
		return this.entityId;
	}
}
