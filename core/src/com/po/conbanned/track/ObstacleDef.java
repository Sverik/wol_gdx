package com.po.conbanned.track;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.po.conbanned.model.Obstacle;

import java.util.concurrent.atomic.AtomicInteger;

public class ObstacleDef {
	private static final AtomicInteger defIdSeq = new AtomicInteger();

	public final String defId = Integer.toString(defIdSeq.incrementAndGet());
	public final Obstacle.Type type;
	public final Shape shape;
	public final Vector2 center;
	public final Vector2 pos1Offset;
	public final String outputDefId;

	public ObstacleDef(Shape shape, Vector2 center) {
		this(Obstacle.Type.FIXED, shape, center, null, null);
	}

	public ObstacleDef(Obstacle.Type type, Shape shape, Vector2 center, Vector2 pos1Offset, String outputDefId) {
		this.type = type;
		this.shape = shape;
		this.center = center;
		this.pos1Offset = pos1Offset;
		this.outputDefId = outputDefId;
	}
}
