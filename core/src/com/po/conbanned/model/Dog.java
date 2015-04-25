package com.po.conbanned.model;

import com.badlogic.gdx.math.Vector2;

public class Dog implements Runner {
    private static final float TURN_SPEED_DEG_PER_SEC = 360f;
    private static final float MOVE_SPEED_UNIT_PER_SEC = 35f;
    private static final float DESTINATION_ARRIVED_THRESHOLD = 0.3f;
    private static final float MOVE_SPEED_DECREASE_FROM_DISTANCE = 5f;
    private static final float ACCELERATION_SPEED_PER_SEC = 40f;

    private Vector2 position;
    private Vector2 orientation;
    private Vector2 velocity;
    private Vector2 desiredMovement;

    public Dog() {
        position = new Vector2();
        orientation = new Vector2();
        velocity = new Vector2();
        desiredMovement = new Vector2();
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public Vector2 getOrientation() {
        return orientation;
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public float getAccelerationSpeedPerSec() {
        return ACCELERATION_SPEED_PER_SEC;
    }

    @Override
    public float getTurnSpeedDegPerSec() {
        return TURN_SPEED_DEG_PER_SEC;
    }

    @Override
    public float getMoveSpeedUnitPerSec() {
        return MOVE_SPEED_UNIT_PER_SEC;
    }

    @Override
    public float getDestinationArrivedThreshold() {
        return DESTINATION_ARRIVED_THRESHOLD;
    }

    @Override
    public float getMoveSpeedDecreaseFromDistance() {
        return MOVE_SPEED_DECREASE_FROM_DISTANCE;
    }


}
