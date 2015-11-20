package com.po.conbanned.controller;

import com.badlogic.gdx.math.Vector2;
import com.po.conbanned.model.Mover;
import com.po.conbanned.model.Obstacle;
import com.po.conbanned.model.Trigger;
import com.po.conbanned.model.Wireable;
import com.po.conbanned.model.World;

public class WireController {
	private final World world;

	public WireController(World world) {
		this.world = world;
	}

	public void update(float delta) {
		for (Wireable wire : world.getWired()) {
			if (wire instanceof Trigger) {
				Trigger trigger = (Trigger) wire;
				trigger.getOutput().setActive(trigger.isActive());
			} else if (wire instanceof Mover) {
				Mover mover = (Mover) wire;
				move(delta, mover, mover.isActive() ? mover.pos1 : mover.pos0, mover.isActive() ? mover.getSpeed01() : mover.getSpeed10());
			}
		}
	}

	private void move(float delta, Mover mover, Vector2 target, float speed) {
		Vector2 movement = new Vector2(target){
			@Override
			public String toString() {
				return "[" + Math.round(x * 10f) / 10f + ":" + Math.round(y * 10f) / 10f + "]";
			}
		}.sub(mover.getBody().getPosition());
		if (movement.len2() > 0.01f) {
			movement.nor().scl(speed * delta);
		} else {
			movement.setZero();
		}
		mover.getBody().setLinearVelocity(movement);
	}
}
