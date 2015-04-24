package com.po.conbanned.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.po.conbanned.model.Dog;
import com.po.conbanned.model.Runner;
import com.po.conbanned.model.World;

import java.util.Iterator;

public class DogController {
    public static final float TRACE_DISTANCE_2 = (float) Math.pow(2f, 2);
    public static final int TRACE_MAX_LENGTH = 50;
    public static final float TRACE_DECAY_PER_SEC = 0.2f / TRACE_MAX_LENGTH;

    private World world;

    public DogController(World world) {
        this.world = world;
    }

    public void update(float delta) {
        if (world.getHoverState() != World.HoverState.NONE) {

            moveTowardsTarget(world.getDog(), world.getHover(), delta);

            if (world.getDogTrace().isEmpty()) {
                world.getDogTrace().add(new Vector3(world.getDog().getPosition(), 1f));
            } else {
                Vector3 lastTrace = world.getDogTrace().getLast();
                if (world.getDog().getPosition().dst2(lastTrace.x, lastTrace.y) >= TRACE_DISTANCE_2) {
                    world.getDogTrace().add(new Vector3(world.getDog().getPosition(), 1f));
                }
            }
            while (world.getDogTrace().size() > TRACE_MAX_LENGTH) {
                world.getDogTrace().removeFirst();
            }
        }
        Iterator<Vector3> traceIter = world.getDogTrace().iterator();
        while (traceIter.hasNext()) {
            Vector3 trace = traceIter.next();
            trace.z -= TRACE_DECAY_PER_SEC;
            if (trace.z <= 0f) {
                traceIter.remove();
            }
        }
    }
    
    public static void moveTowardsTarget(Runner runner, Vector2 target, float delta) {
        float desiredAngleDelta = new Vector2(target).sub(runner.getPosition()).angle(runner.getOrientation());
//            System.out.println(desiredAngleDelta);
        float direction = Math.signum(desiredAngleDelta);
        float magnitude = Math.abs(desiredAngleDelta);
        magnitude = Math.min(magnitude, delta * Dog.TURN_SPEED_DEG_PER_SEC);
        float angleDelta = magnitude * direction;
//            System.out.println(desiredAngleDelta + ", " + angleDelta);
        runner.getOrientation().rotate(-angleDelta);

        float distance = runner.getPosition().dst(target);
        // distance D_F_D...D_A_T -> S...0
        float moveSpeed = Dog.MOVE_SPEED_UNIT_PER_SEC;
        if (distance <= Dog.MOVE_SPEED_DECREASE_FROM_DISTANCE) {
            moveSpeed = (distance - Dog.DESTINATION_ARRIVED_THRESHOLD) / (Dog.MOVE_SPEED_DECREASE_FROM_DISTANCE - Dog.DESTINATION_ARRIVED_THRESHOLD) * Dog.MOVE_SPEED_UNIT_PER_SEC;
            moveSpeed = Math.max(0, moveSpeed);
        }
        Vector2 directionVector = new Vector2(runner.getOrientation()).nor().scl(moveSpeed * delta);
        runner.getPosition().add(directionVector);
    }
}
