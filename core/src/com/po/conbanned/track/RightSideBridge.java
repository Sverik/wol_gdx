package com.po.conbanned.track;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.po.conbanned.model.World;

public class RightSideBridge extends TrackPiece {
    public RightSideBridge(World world) {
        super(world);
        PolygonShape leftRiver = new PolygonShape();
        leftRiver.set(new float[]{
                0, 10,
                World.GRID_WIDTH * 0.65f, 15,
                World.GRID_WIDTH * 0.64f, 45,
                0, 40,
        });

        PolygonShape rightRiver = new PolygonShape();
        rightRiver.set(new float[]{
                World.GRID_WIDTH * 0.8f, 15,
                World.GRID_WIDTH * 0.79f, 45,
                World.GRID_WIDTH, 48,
                World.GRID_WIDTH, 13,
        });

        add(new ObstacleDef(leftRiver, new Vector2(0, 0)));
        add(new ObstacleDef(rightRiver, new Vector2(0, 0)));
    }

    @Override
    public float getLength() {
        return 100f;
    }

}
