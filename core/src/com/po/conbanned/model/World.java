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
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.po.conbanned.track.ObstacleDef;
import com.po.conbanned.track.PlacedPiece;
import com.po.conbanned.track.TrackPiece;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static final int GRID_WIDTH = 64 * 2;
    public static final int GRID_HEIGHT = 48 * 2;

    ArrayList<String> debugText = new ArrayList<String>();

    public float trip = 0f;
    public LinkedList<PlacedPiece> trackPieces = new LinkedList<PlacedPiece>();

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

    public void debug(String line) {
        debugText.add(line);
    }

    public Iterable<String> getDebug() {
        return debugText;
    }

    public void clearDebug() {
        debugText.clear();
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
                Reference refA = (Reference) contact.getFixtureA().getUserData();
                Reference refB = (Reference) contact.getFixtureB().getUserData();
                if (flockingEvent(refA, refB)) {
                    action.action(refA.getSheep(), refB.getSheep());
                }
            }

            private boolean flockingEvent(Reference refA, Reference refB) {
                if (refA.getType() == Reference.Type.SHEEP && refB.getType() == Reference.Type.FLOCK) {
                    return true;
                }
                if (refB.getType() == Reference.Type.SHEEP && refA.getType() == Reference.Type.FLOCK) {
                    return true;
                }
                return false;
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
*/
        for (int i = 0 ; i < 50 ; i++) {
            addSheep((float)Math.random() * GRID_WIDTH, (float)Math.random() * GRID_HEIGHT);
        }

        addObstacle(createObstacle(createRectangularAAObstacle(0, 0, 2, GRID_HEIGHT), 0f));

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
        sheep.getBody().createFixture(fixtureDef).setUserData(Reference.sheep(sheep));
        shape.dispose();

        CircleShape flockShape = new CircleShape();
        flockShape.setRadius(Sheep.FLOCK_RADIUS);
        FixtureDef flockSensorDef = new FixtureDef();
        flockSensorDef.shape = flockShape;
        flockSensorDef.isSensor = true;
        BodyDef flockBodyDef = new BodyDef();
        flockBodyDef.position.set(gridX, gridY);
        Body flockBody = physics.createBody(flockBodyDef);
        flockBody.createFixture(flockSensorDef).setUserData(Reference.flock(sheep, flockBody));
        flockShape.dispose();

    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
        Body body = physics.createBody(obstacle.getBodyDef());
        obstacle.setBody(body);
        obstacle.getBody().createFixture(obstacle.getFixtureDef()).setUserData(Reference.obstacle(body));
//        obstacle.getShape().dispose();
    }

    public ObstacleDef createRectangularAAObstacle(float gx0, float gy0, float gx1, float gy1) {
        Rectangle rect = new Rectangle(Math.min(gx0, gx1), Math.min(gy0, gy1), Math.abs(gx1-gx0), Math.abs(gy1-gy0));
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2, new Vector2(0, 0), 0f);
        return new ObstacleDef(shape, rect.getCenter(new Vector2()));
    }

    public Obstacle createObstacle(ObstacleDef obstacleDef, float tripOffset) {
        Obstacle obstacle = new Obstacle();

        obstacle.setShape(obstacleDef.shape);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(new Vector2(obstacleDef.center).add(0, tripOffset));
        obstacle.setBodyDef(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = obstacleDef.shape;
        obstacle.setFixtureDef(fixtureDef);

        return obstacle;
    }
}
