package com.po.conbanned.track;

import com.po.conbanned.model.World;

import java.util.ArrayList;

public abstract class TrackPiece {
	private ArrayList<ObstacleDef> obstacleDefs = new ArrayList<ObstacleDef>();

	protected TrackPiece(World world) {
//		add(world.createRectangularAAObstacle(0, 45, 10, 50));
		obstacleDefs.add(world.createRectangularAAObstacle(0, 80, 35, 50));
	}

	public abstract float getLength();

	protected void add(ObstacleDef def) {
//		obstacleDefs.add(def);
	}

	public Iterable<ObstacleDef> getObstacleDefs() {
		return obstacleDefs;
	}
}
