package com.po.conbanned.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Dog {
	public static final float RADIUS = 1.4f;
	public static final float MOVE_SCALE = 25f;
	public static final float MAX_MOVE_FORCE = 200f;
	public static final float LINEAR_DAMPING = 1f;
	public static final float STANDING_SCARINESS = 2f;
	public static final float LAYING_SCARINESS = 0f;

	private Vector2 orientation;
	private Vector2 desiredMovement;
	private Body body;
	private float scariness;

	public Dog() {
		orientation = new Vector2();
		desiredMovement = new Vector2();
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public float getScariness() {
		return scariness;
	}

	public void setScariness(float scariness) {
		this.scariness = scariness;
	}

	public Vector2 getDesiredMovement() {
		return desiredMovement;
	}

	public void setDesiredMovement(Vector2 desiredMovement) {
		this.desiredMovement = desiredMovement;
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public Vector2 getOrientation() {
		return orientation;
	}

	public Vector2 getVelocity() {
		return body.getLinearVelocity();
	}


}
