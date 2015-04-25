package com.po.conbanned.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.po.conbanned.model.Sheep;
import com.po.conbanned.model.World;

public class SheepController {
    private static final float TRACE_EFFECT_MAX_DISTANCE = 15f;
    private static final float TRACE_EFFECT = 15f;
    private static final float FLOCK_EFFECT = 0.3f;
    private static final float EFFECTS_NEGLIGIBLE_THRESHOLD = 0.5f;
    private static final float NO_TRACE_EFFECT_DECELERATION = 0.95f;

    private World world;

    public SheepController(World world) {
        this.world = world;
    }

    public void update(float delta) {
        for (Sheep sheep : world.getSheep()) {
            sheep.getDesiredMovement().set(0, 0);

            // Põgenemine koera eest
            Vector2 dogEffect = dogEffect(sheep);

            // Jooksmine lähedalolevate lammaste poole
            Vector2 flockEffect = flockEffect(sheep);

            // Rahulikus olekus suvalises suunas sammu tegemine

            sheep.getDesiredMovement().add(dogEffect).add(flockEffect);

            if (sheep.getDesiredMovement().len() <= EFFECTS_NEGLIGIBLE_THRESHOLD) {
                sheep.getBody().setLinearVelocity(sheep.getBody().getLinearVelocity().scl(NO_TRACE_EFFECT_DECELERATION));
            }

            sheep.getBody().applyForceToCenter(sheep.getDesiredMovement(), true);

            // TODO: pöörame lammast, pole parim lahendus
            if (sheep.getDesiredMovement().len2() > 0.01f) {
                sheep.getOrientation().set(sheep.getDesiredMovement());
            }
        }
    }

    private Vector2 dogEffect(Sheep sheep) {
        Vector2 result = new Vector2();
        Vector2 pos = sheep.getPosition();
        for (Vector3 trace : world.getDogTrace()) {
            float distanceFromTrace = pos.dst(trace.x, trace.y);
            if (distanceFromTrace > TRACE_EFFECT_MAX_DISTANCE) {
                continue;
            }
            Vector2 traceEffect = new Vector2(pos).sub(trace.x, trace.y).nor().scl(distanceFromTrace / TRACE_EFFECT_MAX_DISTANCE * trace.z * TRACE_EFFECT);
            result.add(traceEffect);
        }
        return result;
    }

    private Vector2 flockEffect(Sheep sheep) {
        Vector2 result = new Vector2();
        for (Sheep inFlock : sheep.getFlock()) {
            result.x += inFlock.getPosition().x - sheep.getPosition().x;
            result.y += inFlock.getPosition().y - sheep.getPosition().y;
        }
        return result.scl(FLOCK_EFFECT);
    }

    public void afterPhysics(float delta) {
    }
}
