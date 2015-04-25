package com.po.conbanned.track;

import com.po.conbanned.model.Obstacle;

import java.util.List;

public class PlacedPiece {
    public final TrackPiece track;
    public final List<Obstacle> instantiatedObstacles;
    public final float tripOffset;

    public PlacedPiece(TrackPiece track, List<Obstacle> instantiatedObstacles, float tripOffset) {
        this.track = track;
        this.instantiatedObstacles = instantiatedObstacles;
        this.tripOffset = tripOffset;
    }
}
