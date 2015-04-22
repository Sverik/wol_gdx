package com.po.conbanned.model;

import com.badlogic.gdx.math.Vector2;

public class Dog {
    public static final float TURN_SPEED_DEG_PER_SEC = 360f;
    public static final float MOVE_SPEED_UNIT_PER_SEC = 0.5f;
    public static final float DESTINATION_ARRIVED_THRESHOLD = 0.3f;
    public static final float MOVE_SPEED_DECREASE_FROM_DISTANCE = 5f;

    private Vector2 position;
    private Vector2 orientation;
    private Vector2 desiredMovement;

    public Dog() {
        position = new Vector2();
        orientation = new Vector2();
        desiredMovement = new Vector2();
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getOrientation() {
        return orientation;
    }
}
