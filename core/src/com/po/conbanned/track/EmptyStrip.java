package com.po.conbanned.track;

import com.po.conbanned.model.Obstacle;
import com.po.conbanned.model.World;

import java.util.ArrayList;

public class EmptyStrip extends TrackPiece {

    public EmptyStrip(World world) {
        super(world);
    }

    @Override
    public float getLength() {
        return 10f;
    }

}
