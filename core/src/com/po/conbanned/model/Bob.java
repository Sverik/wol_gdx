package com.po.conbanned.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bob {

	public enum State {
		IDLE, WALKING, JUMPING, DYING
	}

	public static final float SPEED = 4f;    // unit per second
	static final float JUMP_VELOCITY = 1f;
	public static final float SIZE = 0.5f; // half a unit

	Vector2 position = new Vector2();
	Vector2 acceleration = new Vector2();
	Vector2 velocity = new Vector2();
	Rectangle bounds = new Rectangle();
	State state = State.IDLE;
	float stateTime = 0;
	boolean facingLeft = true;

	public Bob(Vector2 position) {
		this.position = position;
		this.bounds.height = SIZE;
		this.bounds.width = SIZE;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setFacingLeft(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}

	public boolean isFacingLeft() {
		return facingLeft;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setState(State newState) {
		this.state = newState;
	}

	public State getState() {
		return state;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void update(float delta) {
		stateTime += delta;
		position.add(velocity.cpy().scl(delta));
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
	}
}
