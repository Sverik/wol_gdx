package com.po.conbanned.track;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

public class ObstacleDef {
    public final Shape shape;
    public final Vector2 center;

    public ObstacleDef(Shape shape, Vector2 center) {
        this.shape = shape;
        this.center = center;
    }
}
