package com.po.conbanned.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

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

    private void createDemoWorld() {
        physics = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0), true);

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
        sheep.getPosition().set(gridX, gridY);
        sheep.getOrientation().set(1, 0);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 0.3f;
        bodyDef.position.set(sheep.getPosition());
        sheep.setBody(physics.createBody(bodyDef));
        CircleShape shape = new CircleShape();
        shape.setRadius(Sheep.RADIUS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        sheep.getBody().createFixture(fixtureDef);
        shape.dispose();
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
