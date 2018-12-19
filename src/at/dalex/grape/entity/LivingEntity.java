package at.dalex.grape.entity;

import java.awt.image.BufferedImage;

import at.dalex.grape.graphics.Animation;
import at.dalex.grape.renderer.graphicsutil.ImageUtils;

public abstract class LivingEntity extends Entity {

	private int type;
	private int health;
	protected MoveDirection facingDirection = MoveDirection.DOWN;

	private boolean hasAnimation = false;
	private Animation animation;

	public LivingEntity(double x, double y, int type, int health) {
		super(x, y);
		this.type = type;
		this.health = health;
	}

	@Override
	public void update(double delta) {
		if (hasAnimation) {
			animation.update(delta);
		}
	}

	public boolean hasAnimation() {
		return hasAnimation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
		hasAnimation = true;
	}

	public void loadAnimation(String animationFile, int frameWidth, int frameHeight, int delay) {
		BufferedImage animationAtlas = ImageUtils.loadBufferedImage(animationFile);
		if (animationAtlas != null) {
			animation = Animation.loadAnimation(animationAtlas, frameWidth, frameHeight, delay);
			hasAnimation = true;
		}
	}

	public Animation getAnimation() {
		return this.animation;
	}

	public void damage(int damage) {
		health -= damage;
		if (health < 0) health = 0;
	}

	public int getHealth() {
		return this.health;
	}

	public int getType() {
		return this.type;
	}

	public MoveDirection getMoveDirection() {
		return this.facingDirection;
	}

}
