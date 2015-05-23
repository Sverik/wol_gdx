package com.po.conbanned.controller;

import com.po.conbanned.model.Obstacle;
import com.po.conbanned.model.Trigger;
import com.po.conbanned.model.Wireable;
import com.po.conbanned.model.World;
import com.po.conbanned.track.DiamondCenter;
import com.po.conbanned.track.EmptyStrip;
import com.po.conbanned.track.Funnel;
import com.po.conbanned.track.ObstacleDef;
import com.po.conbanned.track.PlacedPiece;
import com.po.conbanned.track.RightSideBridge;
import com.po.conbanned.track.TrackPiece;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MapController {
	private static final float MAP_SCROLL_SPEED = 3f;

	private static final AtomicInteger placedPiecekIdSeq = new AtomicInteger();

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
            selected = pieces[2];
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

		String placedPieceId = Integer.toString(placedPiecekIdSeq.incrementAndGet());

		PlacedPiece placed = new PlacedPiece(placedPieceId, piece, tripOffset);
		world.trackPieces.addLast(placed);
		for (ObstacleDef obstacleDef : piece.getObstacleDefs()) {
			Obstacle obstacle = world.createObstacle(placedPieceId, obstacleDef, tripOffset);
			placed.instantiatedObstacles.put(obstacle.defId, obstacle);
			world.addObstacle(obstacle);
		}

		// ühenda väljundid
		for (Obstacle obstacle : placed.instantiatedObstacles.values()) {
			if (obstacle instanceof Trigger) {
				Trigger trigger = (Trigger) obstacle;
				trigger.setOutput((Wireable) placed.instantiatedObstacles.get(trigger.outputId));
			}
		}
	}

	private void remove(PlacedPiece placed) {
		for (Obstacle obstacle : placed.instantiatedObstacles.values()) {
			world.physics.destroyBody(obstacle.getBody());
		}
	}
}
