package com.po.conbanned.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Block {

    public static final float SIZE = 1f;

    Vector2 	position = new Vector2();
    Rectangle 	bounds = new Rectangle();

    public Block(Vector2 pos) {
        this.position = pos;
        bounds.x = position.x;
        bounds.y = position.y;
        this.bounds.width = SIZE;
        this.bounds.height = SIZE;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
