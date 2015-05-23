package com.po.conbanned.model;

import com.badlogic.gdx.math.Vector2;

public class Mover extends Obstacle implements Wireable {

	private static final float OPEN_SPEED = 800f;
	private static final float CLOSE_SPEED = 80f;

	public final Vector2 pos0;
	public final Vector2 pos1;

	private boolean active = false;

	public Mover(String trackId, String defId, Vector2 pos0, Vector2 pos1) {
		super(trackId, defId, Type.MOVING);
		this.pos0 = pos0;
		this.pos1 = pos1;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public Wireable getOutput() {
		return null;
	}

	public float getSpeed01() {
		return OPEN_SPEED;
	}

	public float getSpeed10() {
		return CLOSE_SPEED;
	}
}
