package com.po.conbanned.track;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.po.conbanned.model.Obstacle;

public class ObstacleDef {
	public final Obstacle.Type type;
	public final Shape shape;
	public final Vector2 center;

	public ObstacleDef(Shape shape, Vector2 center) {
		this(Obstacle.Type.FIXED, shape, center);
	}

	public ObstacleDef(Obstacle.Type type, Shape shape, Vector2 center) {
		this.type = type;
		this.shape = shape;
		this.center = center;
	}
}
