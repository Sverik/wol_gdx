package com.po.conbanned.track;

import com.po.conbanned.model.World;

import java.util.ArrayList;

public abstract class TrackPiece {

	private ArrayList<ObstacleDef> obstacleDefs = new ArrayList<ObstacleDef>();

	protected TrackPiece(World world) {
		add(world.createRectangularAAObstacle(0, 0, 2, getLength()));
		add(world.createRectangularAAObstacle(World.GRID_WIDTH, getLength(), World.GRID_WIDTH - 2, 0));
	}

	public abstract float getLength();

	protected void add(ObstacleDef def) {
		obstacleDefs.add(def);
	}

	public Iterable<ObstacleDef> getObstacleDefs() {
		return obstacleDefs;
	}
}
