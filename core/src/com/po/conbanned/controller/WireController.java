package com.po.conbanned.controller;

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
				world.debug("trigger " + wire.getId() + " " + ((Trigger)wire).sheepCount);
			}
		}
	}
}
