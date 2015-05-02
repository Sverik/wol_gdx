package com.po.conbanned.controller;

import com.badlogic.gdx.math.Vector3;
import com.po.conbanned.model.Dog;
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
		Dog dog = world.getDog();

		dog.getDesiredMovement().set(0, 0);
		if (world.getHoverState() == World.HoverState.ACTIVE) {
			// dog is scary
			dog.setScariness(Dog.STANDING_SCARINESS);

			// move
			dog.getDesiredMovement().add(world.getHover()).sub(dog.getPosition()).scl(Dog.MOVE_SCALE);
			dog.getDesiredMovement().clamp(0, Dog.MAX_MOVE_FORCE);

			// trace
			Vector3 lastTrace = null;
			if (world.getDogTrace().isEmpty()) {
				world.getDogTrace().add(new Vector3(dog.getPosition(), 1f));
			} else {
				lastTrace = world.getDogTrace().getLast();
				if (dog.getPosition().dst2(lastTrace.x, lastTrace.y) >= TRACE_DISTANCE_2) {
					world.getDogTrace().add(new Vector3(dog.getPosition(), 1f));
				}
			}
			while (world.getDogTrace().size() > TRACE_MAX_LENGTH) {
				world.getDogTrace().removeFirst();
			}

			// orientation
			if (lastTrace != null) {
				dog.getOrientation().set(dog.getPosition()).sub(lastTrace.x, lastTrace.y).nor();
			}
		} else {
			// TODO: deceleration
//            dog.getVelocity().set(0, 0);
			dog.getBody().setLinearVelocity(0, 0);

			// dog is not scary
			dog.setScariness(Dog.LAYING_SCARINESS);
		}

		// füüsika
		dog.getBody().applyForceToCenter(dog.getDesiredMovement(), true);

		// jäljed kaovad
		Iterator<Vector3> traceIter = world.getDogTrace().iterator();
		while (traceIter.hasNext()) {
			Vector3 trace = traceIter.next();
			trace.z -= TRACE_DECAY_PER_SEC;
			if (trace.z <= 0f) {
				traceIter.remove();
			}
		}
	}

}
