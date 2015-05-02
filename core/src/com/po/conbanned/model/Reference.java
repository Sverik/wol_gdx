package com.po.conbanned.model;

import com.badlogic.gdx.physics.box2d.Body;

public class Reference {
	public enum Type {
		SHEEP,
		DOG,
		FLOCK,
		OBSTACLE,;
	}

	private final Type type;
	private final Sheep sheep;
	private final Dog dog;
	private final Body body;

	private Reference(Type type, Sheep sheep, Dog dog, Body body) {
		this.type = type;
		this.sheep = sheep;
		this.dog = dog;
		this.body = body;
	}

	public Type getType() {
		return type;
	}

	public Sheep getSheep() {
		return sheep;
	}

	public Dog getDog() {
		return dog;
	}

	public Body getBody() {
		if (body != null) {
			return body;
		} else if (sheep != null) {
			return sheep.getBody();
		} else if (dog != null) {
			return null;
		}
		return null;
	}

	public static Reference sheep(Sheep sheep) {
		return new Reference(Type.SHEEP, sheep, null, null);
	}

	public static Reference flock(Sheep sheep, Body flockSensor) {
		return new Reference(Type.FLOCK, sheep, null, flockSensor);
	}

	public static Reference dog(Dog dog) {
		return new Reference(Type.DOG, null, dog, null);
	}

	public static Reference obstacle(Body body) {
		return new Reference(Type.OBSTACLE, null, null, body);
	}

	@Override
	public String toString() {
		return "Reference{" +
				"type=" + type +
				", sheep=" + sheep +
				", dog=" + dog +
				", body=" + body +
				'}';
	}
}
