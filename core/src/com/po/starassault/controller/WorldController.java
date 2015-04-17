package com.po.starassault.controller;

import com.po.starassault.model.Bob;
import com.po.starassault.model.World;

import java.util.HashMap;
import java.util.Map;

public class WorldController {

    enum Keys {
        LEFT, RIGHT, JUMP, FIRE
    }

    private World world;
    private Bob bob;

    static Map<Keys, Boolean> keys = new HashMap<WorldController.Keys, Boolean>();
    static {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.JUMP, false);
        keys.put(Keys.FIRE, false);
    };

    public WorldController(World world) {
        this.world = world;
        this.bob = world.getBob();
    }

    // ** Key presses and touches **************** //

    public void leftPressed() {
        keys.get(keys.put(Keys.LEFT, true));
    }

    public void rightPressed() {
        keys.get(keys.put(Keys.RIGHT, true));
    }

    public void jumpPressed() {
        keys.get(keys.put(Keys.JUMP, true));
    }

    public void firePressed() {
        keys.get(keys.put(Keys.FIRE, false));
    }

    public void leftReleased() {
        keys.get(keys.put(Keys.LEFT, false));
    }

    public void rightReleased() {
        keys.get(keys.put(Keys.RIGHT, false));
    }

    public void jumpReleased() {
        keys.get(keys.put(Keys.JUMP, false));
    }

    public void fireReleased() {
        keys.get(keys.put(Keys.FIRE, false));
    }

    /** The main update method **/
    public void update(float delta) {
        processInput();
        bob.update(delta);
    }

    /** Change Bob's state and parameters based on input controls **/
    private void processInput() {
        if (keys.get(Keys.LEFT)) {
            // left is pressed
            bob.setFacingLeft(true);
            bob.setState(Bob.State.WALKING);
            bob.getVelocity().x = -Bob.SPEED;
        }
        if (keys.get(Keys.RIGHT)) {
            // left is pressed
            bob.setFacingLeft(false);
            bob.setState(Bob.State.WALKING);
            bob.getVelocity().x = Bob.SPEED;
        }
        // need to check if both or none direction are pressed, then Bob is idle
        if ((keys.get(Keys.LEFT) && keys.get(Keys.RIGHT)) ||
                (!keys.get(Keys.LEFT) && !(keys.get(Keys.RIGHT)))) {
            bob.setState(Bob.State.IDLE);
            // acceleration is 0 on the x
            bob.getAcceleration().x = 0;
            // horizontal speed is 0
            bob.getVelocity().x = 0;
        }
    }
}
