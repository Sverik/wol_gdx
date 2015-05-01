package com.po.conbanned.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.HashSet;

public class Sheep {
    public static final float RADIUS = 2.2f;
    public static final float FLOCK_RADIUS = 10f;

    private Vector2 orientation;
    private Vector2 desiredMovement;
    private Body body;
    private HashSet<Sheep> flock = new HashSet<Sheep>();

    public Sheep() {
        orientation = new Vector2();
        desiredMovement = new Vector2();
    }

    public HashSet<Sheep> getFlock() {
        return flock;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Vector2 getOrientation() {
        return orientation;
    }

    public Vector2 getDesiredMovement() {
        return desiredMovement;
    }
}
