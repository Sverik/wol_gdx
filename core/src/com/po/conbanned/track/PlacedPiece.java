package com.po.conbanned.track;

import com.po.conbanned.model.Obstacle;

import java.util.HashMap;
import java.util.List;

public class PlacedPiece {
	public final String placedPieceId;
	public final TrackPiece track;
	public final HashMap<String, Obstacle> instantiatedObstacles = new HashMap<String, Obstacle>();
	public final float tripOffset;

	public PlacedPiece(String placedPieceId, TrackPiece track, float tripOffset) {
		this.placedPieceId = placedPieceId;
		this.track = track;
		this.tripOffset = tripOffset;
	}
}
