package com.po.conbanned.controller;

import com.badlogic.gdx.math.Vector2;
import com.po.conbanned.model.Attacker;
import com.po.conbanned.model.Landmine;
import com.po.conbanned.model.World;
import com.sun.org.apache.bcel.internal.generic.LAND;

import java.util.Iterator;

public class AttackerController {
    private static final int MAX_ATTACKERS = 150;
    private static final float MAX_SPEED = 6f;

    private static final float MAX_DISTANCE = (float) Math.sqrt(Math.pow(World.GRID_WIDTH / 2, 2) + Math.pow(World.GRID_HEIGHT / 2, 2));

    World world;
    float time = 0;

    public AttackerController(World world) {
        this.world = world;
    }

    /** The main update method **/
    public boolean update(float delta) {
        if (world.hqHealth <= 0) {
            return false;
        }
        if (world.hqHealth > 0) {
            return true;
        }

        time += delta;

        // add attackers, if needed
        int maxAttackers = (int) (time / 500 * MAX_ATTACKERS + 5);
        maxAttackers = Math.min(maxAttackers, MAX_ATTACKERS);
        if (world.getAttackers().size() < maxAttackers && Math.random() < 0.1) {
            newAttacker();
        }

        moveAttackers(delta);

        return true;
    }

    private void newAttacker() {
        Vector2 pos = new Vector2(MAX_DISTANCE, 0);
        pos.rotate((float) (Math.random() * 360f)).add(World.GRID_WIDTH / 2, World.GRID_HEIGHT / 2);
        Attacker a = new Attacker(pos);
        world.targetHouse.getCenter(a.getVelocity());
        a.getVelocity().sub(pos);
        a.getVelocity().nor();
        float speed = time / 180 * MAX_SPEED + 1f;
        speed = Math.min(speed, MAX_SPEED);
        a.getVelocity().scl(speed);
        world.getAttackers().add(a);
    }

    private void moveAttackers(float delta) {
        Iterator<Attacker> attIter = world.getAttackers().iterator();
        while (attIter.hasNext()) {
            Attacker a = attIter.next();
            // remove?
            if (a.getBounds().overlaps(world.targetHouse)) {
                attIter.remove();
                world.hqHealth--;
                continue;
            }

            if (a.getState() == Attacker.State.DEAD && a.getSpinningTimeLeft() <= 0) {
                attIter.remove();
                continue;
            }

            // kaboom?
            Vector2 aCenter = a.getBounds().getCenter(new Vector2());
            int gx = (int) a.getPosition().x;
            int gy = (int) a.getPosition().y;
            Landmine closest = null;
            Vector2 closestCenter = new Vector2();
            gridloop: for (int xo = 0 ; xo < Landmine.SIZE ; xo++) {
                for (int yo = 0 ; yo < Landmine.SIZE ; yo++) {
                    Object o = world.getGrid(gx + xo,gy + yo);
                    if (o != null && o instanceof Landmine) {
                        Landmine curr = (Landmine)o;
                        if (closest == null) {
                            closest = curr;
                            closestCenter = closest.getBounds().getCenter(closestCenter);
                            continue;
                        }
                        Vector2 currentCenter = curr.getBounds().getCenter(new Vector2());
                        if (aCenter.dst2(currentCenter) < aCenter.dst2(closestCenter)) {
                            closest = curr;
                            closestCenter = closest.getBounds().getCenter(closestCenter);
                            continue;
                        }
                    }
                }
            }
            if (closest != null) {
                // kaboom!
                Vector2 attackerCenter = new Vector2();
                for (Attacker attacker : world.getAttackers()) {
                    attacker.getBounds().getCenter(attackerCenter);
                    if (attackerCenter.dst(closestCenter) <= Landmine.BLAST_RADIUS) {
                        attacker.setState(Attacker.State.DEAD);
                        world.killCount++;
                    }
                }
                world.remove(closest);
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
