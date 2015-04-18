package com.po.conbanned.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.po.conbanned.model.Landmine;
import com.po.conbanned.model.World;
import com.po.conbanned.view.WorldRenderer;

public class HoverController {
    World world;
    WorldRenderer worldRenderer;

    public HoverController(World world, WorldRenderer worldRenderer) {
        this.world = world;
        this.worldRenderer = worldRenderer;
    }

    public void hover(int sx, int sy) {
        worldRenderer.screenToTile(sx - 100, sy, world.getHover());
        world.setHoverState(World.HoverState.PLACING_LANDMINE);
    }

    public void place(int x, int y) {
        world.add(new Landmine(new Vector2(world.getHover())));
        world.setHoverState(World.HoverState.NONE);
    }
}
