package com.po.conbanned.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.po.conbanned.model.Sheep;
import com.po.conbanned.model.World;

public class SheepController {
    private static final float TRACE_EFFECT_MAX_DISTANCE = 15f;

    private World world;

    public SheepController(World world) {
        this.world = world;
    }

    public void update(float delta) {
        for (Sheep sheep : world.getSheep()) {
            sheep.getDesiredMovement().set(0, 0);

            Vector2 pos = sheep.getPosition();
            for (Vector3 trace : world.getDogTrace()) {
                float distanceFromTrace = pos.dst(trace.x, trace.y);
                if (distanceFromTrace > TRACE_EFFECT_MAX_DISTANCE) {
                    continue;
                }
                Vector2 traceEffect = new Vector2(pos).sub(trace.x, trace.y).nor().scl(distanceFromTrace / TRACE_EFFECT_MAX_DISTANCE * trace.z);
                sheep.getDesiredMovement().add(traceEffect);
            }

            if (sheep.getDesiredMovement().len2() > 0f) {
                Vector2 target = new Vector2(sheep.getDesiredMovement()).add(sheep.getPosition());
                target.rotate((float) Math.random() * 5f * delta);
                DogController.moveTowardsTarget(sheep, target, delta);
            } else {
                // TODO: deceleration
                sheep.getVelocity().set(0, 0);
            }
        }
    }
}
