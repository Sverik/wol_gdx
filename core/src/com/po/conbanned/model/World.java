package com.po.conbanned.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class World {

    public static enum HoverState {
        PLACING_LANDMINE_NP,
        PLACING_LANDMINE,
        NONE,
        ;
    }

    public static final int GRID_WIDTH = 64;
    public static final int GRID_HEIGHT = 48;
    public static final int MAX_HQ_HEALTH = 20;

    Dog dog;
    LinkedList<Vector3> dogTrace = new LinkedList<Vector3>();

    /**
     * The blocks making up the world *
     */
    Array<Block> blocks = new Array<Block>();
    /**
     * Our player controlled hero *
     */
    Bob bob;

    LinkedList<Attacker> attackers = new LinkedList<Attacker>();

    HashSet<Landmine> landmines = new HashSet<Landmine>();

    HoverState hoverState = HoverState.NONE;
    Vector2 hover = new Vector2();

    Object[][] grid = new Object[GRID_WIDTH][GRID_HEIGHT];

    public final Rectangle targetHouse = new Rectangle(GRID_WIDTH / 2 - 3 , GRID_HEIGHT / 2 - 3 , 6 , 6);

    public int hqHealth = MAX_HQ_HEALTH;

    public int killCount = 0;

    public World() {
        createDemoWorld();
    }

    // Getters -----------
    public Array<Block> getBlocks() {
        return blocks;
    }
    // --------------------

    public Bob getBob() {
        return bob;
    }

    public Dog getDog() {
        return dog;
    }

    public LinkedList<Vector3> getDogTrace() {
        return dogTrace;
    }

    /**
     * Grid coordinates.
     * @return
     */
    public Vector2 getHover() {
        return hover;
    }

    public HoverState getHoverState() {
        return hoverState;
    }

    public void setHoverState(HoverState hoverState) {
        this.hoverState = hoverState;
    }

    public LinkedList<Attacker> getAttackers() {
        return attackers;
    }

    public HashSet<Landmine> getLandmines() {
        return landmines;
    }

    public boolean canPlaceLandmineToCurrentHover() {
        int gx = (int) hover.x;
        int gy = (int) hover.y;
        try {
            for (int xo = 0; xo < Landmine.SIZE; xo++) {
                for (int yo = 0; yo < Landmine.SIZE; yo++) {
                    if (grid[gx + xo][gy + yo] != null) {
                        return false;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            return false;
        }
        return true;
    }

    public boolean add(Landmine landmine) {
        if (! canPlaceLandmineToCurrentHover()) {
            return false;
        }
        landmines.add(landmine);
        int gx = (int) landmine.getPosition().x;
        int gy = (int) landmine.getPosition().y;
        for (int xo = 0 ; xo < Landmine.SIZE ; xo++) {
            for (int yo = 0 ; yo < Landmine.SIZE ; yo++) {
                grid[gx + xo][gy + yo] = landmine;
            }
        }
        return true;
    }

    public void remove(Landmine landmine) {
        landmines.remove(landmine);
        int gx = (int) landmine.getPosition().x;
        int gy = (int) landmine.getPosition().y;
        for (int xo = 0 ; xo < Landmine.SIZE ; xo++) {
            for (int yo = 0 ; yo < Landmine.SIZE ; yo++) {
                grid[gx + xo][gy + yo] = null;
            }
        }
    }

    public Object getGrid(int x, int y) {
        if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) {
            return null;
        }
        return grid[x][y];
    }

    private void createDemoWorld() {
        dog = new Dog();
        dog.getPosition().set(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        dog.getOrientation().set(2, 1);
/*
        add(new Landmine(new Vector2(28,16)));
        add(new Landmine(new Vector2(30,15)));
        for (int x = 4 ; x < 31 ; x+=Landmine.SIZE) {
            add(new Landmine(new Vector2(x,11)));
        }

*/
        bob = new Bob(new Vector2(7, 2));

        for (int i = 0; i < 10; i++) {
            blocks.add(new Block(new Vector2(i, 0)));
            blocks.add(new Block(new Vector2(i, 6)));
            if (i > 2)
                blocks.add(new Block(new Vector2(i, 1)));
        }
        blocks.add(new Block(new Vector2(9, 2)));
        blocks.add(new Block(new Vector2(9, 3)));
        blocks.add(new Block(new Vector2(9, 4)));
        blocks.add(new Block(new Vector2(9, 5)));

        blocks.add(new Block(new Vector2(6, 3)));
        blocks.add(new Block(new Vector2(6, 4)));
        blocks.add(new Block(new Vector2(6, 5)));
    }
}
