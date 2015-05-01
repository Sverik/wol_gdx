package com.po.conbanned.controller;

import com.po.conbanned.model.World;
import com.po.conbanned.view.WorldRenderer;

public class HoverController {
    World world;
    WorldRenderer worldRenderer;

    public HoverController(World world, WorldRenderer worldRenderer) {
        this.world = world;
        this.worldRenderer = worldRenderer;
    }

    public void down(int sx, int sy) {
        worldRenderer.screenToTile(sx, sy, world.getHover());
        world.setHoverState(World.HoverState.ACTIVE);
    }

    public void up(int sx, int sy) {
        worldRenderer.screenToTile(sx, sy, world.getHover());
        world.setHoverState(World.HoverState.NONE);
    }
}
