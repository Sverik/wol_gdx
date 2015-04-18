package com.po.conbanned.controller;

import com.badlogic.gdx.math.Vector2;
import com.po.conbanned.model.Attacker;
import com.po.conbanned.model.Landmine;
import com.po.conbanned.model.World;
import com.sun.org.apache.bcel.internal.generic.LAND;

import java.util.Iterator;

public class AttackerController {
    private static final int MAX_ATTACKERS = 20;
    private static final float MAX_SPEED = 2f;

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

            if (a.getState() == Attacker.State.DEAD && a.getSpinningTimeLeft() <= 0) {
                attIter.remove();
                continue;
            }

            // kaboom?
            int gx = (int) a.getPosition().x;
            int gy = (int) a.getPosition().y;
            Landmine mine = null;
            gridloop: for (int xo = 0 ; xo < Landmine.SIZE ; xo++) {
                for (int yo = 0 ; yo < Landmine.SIZE ; yo++) {
                    Object o = world.getGrid(gx + xo,gy + yo);
                    if (o != null && o instanceof Landmine) {
                        mine = (Landmine)o;
                        break gridloop;
                    }
                }
            }
            if (mine != null) {
                // kaboom!
                world.remove(mine);
                a.setState(Attacker.State.DEAD);
                continue;
            }
            
            if (a.getState() == Attacker.State.ALIVE) {
                // move
                a.getPosition().add(new Vector2(a.getVelocity()).scl(delta));
                a.getBounds().setPosition(a.getPosition());
            } else if (a.getState() == Attacker.State.DEAD) {
                // rotate
                a.getVelocity().rotate( delta * 1000 );
                a.subtractSpinningTime(delta);
            }
        }
    }

}
