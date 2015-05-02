package com.po.conbanned.controller;

import com.po.conbanned.model.World;
import com.po.conbanned.view.WorldRenderer;

public class HoverController {
	private static final int POINTER_TARGET_DIFF = 100;

	World world;
	WorldRenderer worldRenderer;

	public HoverController(World world, WorldRenderer worldRenderer) {
		this.world = world;
		this.worldRenderer = worldRenderer;
	}

	public void down(int sx, int sy) {
		worldRenderer.screenToTile(sx, sy - POINTER_TARGET_DIFF, world.getHover());
		world.setHoverState(World.HoverState.ACTIVE);
	}

	public void up(int sx, int sy) {
		worldRenderer.screenToTile(sx, sy - POINTER_TARGET_DIFF, world.getHover());
		world.setHoverState(World.HoverState.NONE);
	}

	public void showFlock(int sx, int sy) {
		worldRenderer.screenToTile(sx, sy - POINTER_TARGET_DIFF, world.debugCoords);
		world.debugRequest = World.DebugRequest.FLOCK;
	}
}
