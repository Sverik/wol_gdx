package com.po.conbanned.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Attacker {
    public static enum State {
        ALIVE,
        DEAD,
        ;
    }
    public static final float SIZE = 1.2f;
    public static final float MAX_SPINNING_TIME = 2;

    Vector2 position = new Vector2();
    Vector2 velocity = new Vector2();
    Rectangle bounds = new Rectangle();

    State state;

    float spinningTimeLeft = MAX_SPINNING_TIME;

    public Attacker(Vector2 pos) {
        state = State.ALIVE;
        position = pos;
        bounds.x = position.x;
        bounds.y = position.y;
        bounds.width = SIZE;
        bounds.height = SIZE;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        bounds.x = position.x;
        bounds.y = position.y;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public float getSpinningTimeLeft() {
        return spinningTimeLeft;
    }

    public void subtractSpinningTime(float delta) {
        this.spinningTimeLeft -= delta;
    }
}
