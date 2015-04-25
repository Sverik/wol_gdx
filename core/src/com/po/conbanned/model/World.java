package com.po.conbanned.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class World {

    public static enum HoverState {
        PLACING_LANDMINE_NP,
        PLACING_LANDMINE,
        NONE,
        ;
    }

    public static final int GRID_WIDTH = 64 * 2;
    public static final int GRID_HEIGHT = 48 * 2;

    Dog dog;
    LinkedList<Vector3> dogTrace = new LinkedList<Vector3>();
    LinkedList<Sheep> sheep = new LinkedList<Sheep>();
    LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();

    public com.badlogic.gdx.physics.box2d.World physics;

    HoverState hoverState = HoverState.NONE;
    Vector2 hover = new Vector2();

    public World() {
        createDemoWorld();
    }

    public Dog getDog() {
        return dog;
    }

    public LinkedList<Vector3> getDogTrace() {
        return dogTrace;
    }

    public LinkedList<Sheep> getSheep() {
        return sheep;
    }

    public LinkedList<Obstacle> getObstacles() {
        return obstacles;
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

    private interface FlockAction {
        public void action(Sheep sheepA, Sheep sheepB);
    }

    private void createDemoWorld() {
        physics = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0), true);

        physics.setContactListener(new ContactListener() {
            private FlockAction begin = new FlockAction() {
                @Override
                public void action(Sheep sheepA, Sheep sheepB) {
                    sheepA.getFlock().add(sheepB);
                    sheepB.getFlock().add(sheepA);
                }
            };

            private FlockAction end = new FlockAction() {
                @Override
                public void action(Sheep sheepA, Sheep sheepB) {
                    sheepA.getFlock().remove(sheepB);
                    sheepB.getFlock().remove(sheepA);
                }
            };

            private void event(Contact contact, FlockAction action) {
                Object udA = contact.getFixtureA().getUserData();
                Object udB = contact.getFixtureB().getUserData();
                if (udA != null && udB != null && udA != udB && (udA instanceof Sheep) && (udB instanceof Sheep)) {
                    Sheep sA = (Sheep) udA;
                    Sheep sB = (Sheep) udB;

                    action.action(sA, sB);
                }
            }

            @Override
            public void beginContact(Contact contact) {
                event(contact, begin);
            }

            @Override
            public void endContact(Contact contact) {
                event(contact, end);
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        dog = new Dog();
        dog.getPosition().set(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        dog.getOrientation().set(2, 1);

        addSheep(GRID_WIDTH / 2, GRID_HEIGHT / 2);
/*
        addSheep(GRID_WIDTH / 4, GRID_HEIGHT / 3);
        addSheep(GRID_WIDTH / 3, GRID_HEIGHT / 4);
*/
        for (int i = 0 ; i < 50 ; i++) {
            addSheep((float)Math.random() * GRID_WIDTH, (float)Math.random() * GRID_HEIGHT);
        }

        addObstacle(0, 0, 2, GRID_HEIGHT);
        addObstacle(0, GRID_HEIGHT, GRID_WIDTH, GRID_HEIGHT - 2);
        addObstacle(GRID_WIDTH, GRID_HEIGHT, GRID_WIDTH - 2, 0);
        addObstacle(GRID_WIDTH, 0, 0, 2);

    }

    private void addSheep(float gridX, float gridY) {
        Sheep sheep = new Sheep();
        this.sheep.add(sheep);
        sheep.getOrientation().set(1, 0);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 0.3f;
        bodyDef.position.set(gridX, gridY);
        sheep.setBody(physics.createBody(bodyDef));
        CircleShape shape = new CircleShape();
        shape.setRadius(Sheep.RADIUS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        sheep.getBody().createFixture(fixtureDef).setUserData(sheep);
        shape.dispose();

        CircleShape flockShape = new CircleShape();
        flockShape.setRadius(Sheep.FLOCK_RADIUS);
        FixtureDef flockSensorDef = new FixtureDef();
        flockSensorDef.shape = flockShape;
        flockSensorDef.isSensor = true;
        BodyDef flockBodyDef = new BodyDef();
        flockBodyDef.position.set(gridX, gridY);
        Body flockBody = physics.createBody(flockBodyDef);
        flockBody.createFixture(flockSensorDef).setUserData(sheep);
        flockShape.dispose();

    }

    private void addObstacle(float gx0, float gy0, float gx1, float gy1) {
        Obstacle obstacle = new Obstacle();
        obstacles.add(obstacle);

        Rectangle rect = new Rectangle(Math.min(gx0, gx1), Math.min(gy0, gy1), Math.abs(gx1-gx0), Math.abs(gy1-gy0));
        obstacle.setShape(rect);
        Vector2 pos = new Vector2();
        pos = rect.getCenter(pos);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(pos);

        Body body = physics.createBody(bodyDef);
        obstacle.setBody(body);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2, new Vector2(0, 0), 0f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        obstacle.getBody().createFixture(fixtureDef);
        shape.dispose();
    }
}
