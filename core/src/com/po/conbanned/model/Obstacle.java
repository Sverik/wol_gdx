package com.po.conbanned.model;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

public class Obstacle {
	public enum Type {
		FIXED,
		MOVING,
		TRIGGER,
		;
	}

	public final String trackId;
	public final String defId;
	private final Type type;
	private Body body;
	private Shape shape;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;

	public Obstacle(String trackId, String defId) {
		this(trackId, defId, Type.FIXED);
	}

	protected Obstacle(String trackId, String defId, Type type) {
		this.trackId = trackId;
		this.defId = defId;
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public Body getBody() {
		return body;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Shape getShape() {
		return shape;
	}

	public void setFixtureDef(FixtureDef fixtureDef) {
		this.fixtureDef = fixtureDef;
	}

	public FixtureDef getFixtureDef() {
		return fixtureDef;
	}

	public BodyDef getBodyDef() {
		return bodyDef;
	}

	public void setBodyDef(BodyDef bodyDef) {
		this.bodyDef = bodyDef;
	}

	public String toIdString() {
		return trackId + "-" + defId;
	}
}
