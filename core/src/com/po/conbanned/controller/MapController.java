package com.po.conbanned.controller;

import com.po.conbanned.model.Obstacle;
import com.po.conbanned.model.World;
import com.po.conbanned.track.DiamondCenter;
import com.po.conbanned.track.EmptyStrip;
import com.po.conbanned.track.Funnel;
import com.po.conbanned.track.ObstacleDef;
import com.po.conbanned.track.PlacedPiece;
import com.po.conbanned.track.RightSideBridge;
import com.po.conbanned.track.TrackPiece;

import java.util.LinkedList;

public class MapController {
	private static final float MAP_SCROLL_SPEED = 3f;

	private TrackPiece[] pieces;

	private World world;

	public MapController(World world) {
		this.world = world;
		pieces = new TrackPiece[]{
				new EmptyStrip(world),
				new DiamondCenter(world),
				new Funnel(world),
				new RightSideBridge(world),
		};
		addPiece(pieces[0], 0f);
	}

	public void update(float delta) {
		world.trip += delta * MAP_SCROLL_SPEED;

		world.debug("trip = " + world.trip);
		world.debug("track pieces = " + world.trackPieces.size());
		world.debug("sheep count = " + world.getSheep().size());

		if (world.trip + World.GRID_HEIGHT - 5f > world.trackPieces.getLast().tripOffset + world.trackPieces.getLast().track.getLength()) {
			TrackPiece selected = pieces[0];
			if (Math.random() < 0.2) {
				selected = pieces[((int) (Math.floor(Math.random() * (pieces.length - 1)) + 1))];
			}
//            selected = pieces[0];
			addPiece(selected);
		}

		// TODO: miks alati ei eemaldata?
		PlacedPiece first = world.trackPieces.getFirst();
		if (world.trip > first.tripOffset + first.track.getLength() - 10) {
			world.trackPieces.removeFirst();
			remove(first);
		}
	}

	private void addPiece(TrackPiece piece) {
		addPiece(piece, world.trackPieces.getLast().tripOffset + world.trackPieces.getLast().track.getLength());
	}

	private void addPiece(TrackPiece piece, float tripOffset) {
		PlacedPiece placed = new PlacedPiece(piece, new LinkedList<Obstacle>(), tripOffset);
		world.trackPieces.addLast(placed);
		for (ObstacleDef obstacleDef : piece.getObstacleDefs()) {
			Obstacle obstacle = world.createObstacle(obstacleDef, tripOffset);
			placed.instantiatedObstacles.add(obstacle);
			world.addObstacle(obstacle);
		}
	}

	private void remove(PlacedPiece placed) {
		for (Obstacle obstacle : placed.instantiatedObstacles) {
			world.physics.destroyBody(obstacle.getBody());
		}
	}
}
