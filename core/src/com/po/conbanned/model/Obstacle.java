package com.po.conbanned.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;

public class Obstacle {
    private Body body;
    private Rectangle shape;

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    public void setShape(Rectangle shape) {
        this.shape = shape;
    }

    public Rectangle getShape() {
        return shape;
    }
}
