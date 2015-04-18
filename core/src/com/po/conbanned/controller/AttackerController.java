package com.po.conbanned.controller;

import com.badlogic.gdx.math.Vector2;
import com.po.conbanned.model.Attacker;
import com.po.conbanned.model.World;

import java.util.Iterator;

public class AttackerController {
    private static final int MAX_ATTACKERS = 50;
    private static final float MAX_SPEED = 4f;

    World world;

    public AttackerController(World world) {
        this.world = world;
    }

    /** The main update method **/
    public void update(float delta) {
        // add attackers, if needed
        if (world.getAttackers().size() < MAX_ATTACKERS && Math.random() < 0.1) {
            newAttacker();
        }

        moveAttackers(delta);

    }

    private void newAttacker() {
        Vector2 pos = new Vector2((float)Math.random() * World.GRID_WIDTH, (float)Math.random() * World.GRID_HEIGHT);
        Attacker a = new Attacker(pos);
        world.targetHouse.getCenter(a.getVelocity());
        a.getVelocity().sub(pos);
        a.getVelocity().nor();
        a.getVelocity().scl(MAX_SPEED);
        world.getAttackers().add(a);
    }

    private void moveAttackers(float delta) {
        Iterator<Attacker> attIter = world.getAttackers().iterator();
        while (attIter.hasNext()) {
            Attacker a = attIter.next();
            // remove?
            if (a.getBounds().overlaps(world.targetHouse)) {
                attIter.remove();
                continue;
            }

            // move
            a.getPosition().add(new Vector2(a.getVelocity()).scl(delta));
            a.getBounds().setPosition(a.getPosition());
        }
    }

}
