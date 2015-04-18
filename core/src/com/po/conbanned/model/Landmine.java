package com.po.conbanned.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Landmine {
    public static final float SIZE = 2f;

    Vector2 position;
    Rectangle bounds;

    public Landmine(Vector2 position) {
        this.position = position;
        bounds = new Rectangle(0, 0, SIZE, SIZE);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        bounds.x = position.x;
        bounds.y = position.y;
    }

}
