package com.po.conbanned.track;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.po.conbanned.model.World;

public class DiamondCenter extends TrackPiece {

	public DiamondCenter(World world) {
		super(world);
		Rectangle rect = new Rectangle(World.GRID_WIDTH / 2 - 20, 10, 40, 40);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2, new Vector2(0, 0), (float) (Math.PI / 4));

		add(new ObstacleDef(shape, rect.getCenter(new Vector2())));

	}

	public float getLength() {
		return 60f;
	}

}
