package com.po.conbanned.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.po.conbanned.model.Dog;
import com.po.conbanned.model.Sheep;
import com.po.conbanned.model.World;

import java.util.Iterator;

public class SheepController {
	private static final float TRACE_EFFECT_MAX_DISTANCE = 25f;
	private static final float TRACE_EFFECT = 20f;
	private static final float FLOCK_CENTER_EFFECT = 1f;
	private static final float FLOCK_ALIGNMENT_EFFECT = 1f;
	private static final float DOG_EFFECT_NEGLIGIBLE_THRESHOLD_2 = 0.0001f;
	private static final float NO_EFFECT_DECELERATION = 0.2f;

	private World world;

	public SheepController(World world) {
		this.world = world;
	}

	public void update(float delta) {
		Iterator<Sheep> iter = world.getSheep().iterator();
		while (iter.hasNext()) {
			Sheep sheep = iter.next();

			if (sheep.getPosition().y + Sheep.RADIUS < world.trip) {
				iter.remove();
				// TODO: see keerab vist asja tuksi, aga miks?
//				world.physics.destroyBody(sheep.getBody());
			}

			sheep.getDesiredMovement().set(0, 0);

			// Põgenemine koera eest
			Vector2 dogEffect = dogEffect(sheep);
			sheep.dbgDogEffect = dogEffect;

			// Jooksmine karja keskele
			Vector2 flockCenterEffect = flockCenterEffect(sheep);
			sheep.dbgFlockCenterEffect = flockCenterEffect;

			// Jooksmine karja üldises suunas
			Vector2 flockAlignmentEffect = flockAlignmentEffect(sheep);
			sheep.dbgFlockAlignmentEffect = flockAlignmentEffect;

			// Rahulikus olekus suvalises suunas sammu tegemine

			// Kõik jõud kokku arvestada
			sheep.getDesiredMovement().add(dogEffect).add(flockCenterEffect).add(flockAlignmentEffect);

			if (dogEffect.len2() <= DOG_EFFECT_NEGLIGIBLE_THRESHOLD_2) {
				sheep.getBody().setLinearVelocity(sheep.getBody().getLinearVelocity().scl((float) Math.pow(NO_EFFECT_DECELERATION, delta)));
			}

			sheep.getBody().applyForceToCenter(sheep.getDesiredMovement(), true);

			Vector2 movement = new Vector2(sheep.getPosition()).sub(sheep.getLastKnownPos());
			if (movement.len2() > 0.001f) {
				sheep.getOrientation().set(movement);
			}
			sheep.getLastKnownPos().set(sheep.getPosition());
		}
	}

	private Vector2 flockAlignmentEffect(Sheep sheep) {
		Vector2 result = new Vector2();
		for (Sheep inFlock : sheep.getFlock()) {
			result.add(inFlock.getOrientation());
		}
		return result.scl(FLOCK_ALIGNMENT_EFFECT);
	}

	private Vector2 dogEffect(Sheep sheep) {
		Vector2 result = new Vector2();
		Vector2 pos = sheep.getPosition();
		// Dog trace is scary
		for (Vector3 trace : world.getDogTrace()) {
			addScareEffect(trace.x, trace.y, trace.z, pos, result);
		}
		// Dog is scary
		Dog dog = world.getDog();
		addScareEffect(dog.getPosition().x, dog.getPosition().y, dog.getScariness(), pos, result);
		return result;
	}

	private void addScareEffect(float x, float y, float magnitude, Vector2 sheepPos, Vector2 result) {
		float distanceFromTrace = sheepPos.dst(x, y);
		if (distanceFromTrace > TRACE_EFFECT_MAX_DISTANCE) {
			return;
		}
		Vector2 traceEffect = new Vector2(sheepPos).sub(x, y).nor().scl(distanceFromTrace / TRACE_EFFECT_MAX_DISTANCE * magnitude * TRACE_EFFECT);
		result.add(traceEffect);
	}

	private Vector2 flockCenterEffect(Sheep sheep) {
		Vector2 result = new Vector2();
		for (Sheep inFlock : sheep.getFlock()) {
			result.x += inFlock.getPosition().x - sheep.getPosition().x;
			result.y += inFlock.getPosition().y - sheep.getPosition().y;
		}
		return result.scl(FLOCK_CENTER_EFFECT);
	}

	public void afterPhysics(float delta) {
	}
}
