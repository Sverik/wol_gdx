package com.po.conbanned.model;

import com.badlogic.gdx.physics.box2d.Body;

public class Reference {
	public enum Type {
		SHEEP,
		DOG,
		FLOCK,
		OBSTACLE,
		WIRE,
		;
	}

	private final Type type;
	private final Sheep sheep;
	private final Dog dog;
	private final Body body;
	private final Obstacle obstacle;

	private Reference(Type type, Sheep sheep, Dog dog, Body body, Obstacle obstacle) {
		this.type = type;
		this.sheep = sheep;
		this.dog = dog;
		this.obstacle = obstacle;
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

	public Obstacle getObstacle() {
		return obstacle;
	}

	public Body getBody() {
		if (body != null) {
			return body;
		} else if (sheep != null) {
			return sheep.getBody();
		} else if (obstacle != null) {
			return obstacle.getBody();
		} else if (dog != null) {
			return null;
		}
		return null;
	}

	public static Reference sheep(Sheep sheep) {
		return new Reference(Type.SHEEP, sheep, null, null, null);
	}

	public static Reference flock(Sheep sheep, Body flockSensor) {
		return new Reference(Type.FLOCK, sheep, null, flockSensor, null);
	}

	public static Reference dog(Dog dog) {
		return new Reference(Type.DOG, null, dog, null, null);
	}

	public static Reference obstacle(Obstacle obstacle) {
		if (obstacle instanceof Trigger) {
			return new Reference(Type.WIRE, null, null, obstacle.getBody(), obstacle);
		}
		return new Reference(Type.OBSTACLE, null, null, obstacle.getBody(), obstacle);
	}

	@Override
	public String toString() {
		return "Reference{" +
				"type=" + type +
				", sheep=" + sheep +
				", dog=" + dog +
				", body=" + body +
				", obstacle=" + obstacle +
				'}';
	}
}
