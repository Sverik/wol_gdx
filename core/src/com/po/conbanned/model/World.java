package com.po.conbanned.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.po.conbanned.model.util.ContactFilter;
import com.po.conbanned.model.util.ContactListener;
import com.po.conbanned.track.ObstacleDef;
import com.po.conbanned.track.PlacedPiece;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class World {

	public static enum HoverState {
		ACTIVE,
		NONE,;
	}

	public static final int GRID_WIDTH = 90;
	public static final int GRID_HEIGHT = 150;

	ArrayList<String> debugText = new ArrayList<String>();

	public float trip = 0f;
	public LinkedList<PlacedPiece> trackPieces = new LinkedList<PlacedPiece>();

	Dog dog;
	LinkedList<Vector3> dogTrace = new LinkedList<Vector3>();
	LinkedList<Sheep> sheep = new LinkedList<Sheep>();
	LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();
	LinkedList<Wireable> wired = new LinkedList<Wireable>();

	public com.badlogic.gdx.physics.box2d.World physics;

	HoverState hoverState = HoverState.NONE;
	Vector2 hover = new Vector2();

	public Vector2 debugCoords = new Vector2();
	public DebugRequest debugRequest = null;

	public enum DebugRequest {
		FLOCK,;
	}

	public Sheep showFlock = null;

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

	public LinkedList<Wireable> getWired() {
		return wired;
	}

	/**
	 * Grid coordinates.
	 *
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
		physics = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, -2), true);

		physics.setContactFilter(new ContactFilter());

		physics.setContactListener(new ContactListener());

		dog = new Dog();
		dog.getOrientation().set(2, 1);
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.linearDamping = Dog.LINEAR_DAMPING;
		bodyDef.position.set(GRID_WIDTH / 2, GRID_HEIGHT / 2);
		dog.setBody(physics.createBody(bodyDef));
		CircleShape shape = new CircleShape();
		shape.setRadius(Dog.RADIUS);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		dog.getBody().createFixture(fixtureDef).setUserData(Reference.dog(dog));
		shape.dispose();

		addSheep(GRID_WIDTH / 2, GRID_HEIGHT / 2);
/*
*/
		for (int i = 0; i < 50; i++) {
			addSheep((float) Math.random() * GRID_WIDTH, (float) Math.random() * GRID_HEIGHT);
		}

	}

	private void addSheep(float gridX, float gridY) {
		Sheep sheep = new Sheep();
		this.sheep.add(sheep);
		sheep.getOrientation().set(1, 0);
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.linearDamping = 0.1f;
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
		sheep.getBody().createFixture(flockSensorDef).setUserData(Reference.flock(sheep, sheep.getBody()));
		flockShape.dispose();

	}

	public void addObstacle(Obstacle obstacle) {
		obstacles.add(obstacle);
		if (obstacle instanceof Wireable) {
			wired.add((Wireable) obstacle);
		}
		Body body = physics.createBody(obstacle.getBodyDef());
		obstacle.setBody(body);
		obstacle.getBody().createFixture(obstacle.getFixtureDef()).setUserData(Reference.obstacle(obstacle));
//        obstacle.getShape().dispose();
	}

	public ObstacleDef createRectangularAAObstacle(float gx0, float gy0, float gx1, float gy1) {
		Rectangle rect = new Rectangle(Math.min(gx0, gx1), Math.min(gy0, gy1), Math.abs(gx1 - gx0), Math.abs(gy1 - gy0));
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2, new Vector2(0, 0), 0f);
		return new ObstacleDef(shape, rect.getCenter(new Vector2()));
	}

	public Obstacle createObstacle(String placedPieceId, ObstacleDef obstacleDef, float tripOffset) {
		Obstacle obstacle;
		if (obstacleDef.type == Obstacle.Type.TRIGGER) {
			obstacle = new Trigger(placedPieceId, obstacleDef.defId, obstacleDef.outputDefId);
		} else if (obstacleDef.type == Obstacle.Type.MOVING) {
			Vector2 pos0 = new Vector2(0, tripOffset).add(obstacleDef.center);
			Vector2 pos1 = new Vector2(pos0).add(obstacleDef.pos1Offset);
			obstacle = new Mover(placedPieceId, obstacleDef.defId, pos0, pos1);
		} else {
			obstacle = new Obstacle(placedPieceId, obstacleDef.defId);
		}

		obstacle.setShape(obstacleDef.shape);
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = obstacleDef.type == Obstacle.Type.MOVING ? BodyDef.BodyType.KinematicBody : BodyDef.BodyType.StaticBody;
		bodyDef.position.set(new Vector2(0, tripOffset).add(obstacleDef.center));
		obstacle.setBodyDef(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = obstacleDef.shape;
		if (obstacleDef.type == Obstacle.Type.TRIGGER) {
			fixtureDef.isSensor = true;
		}
		obstacle.setFixtureDef(fixtureDef);

		return obstacle;
	}
}
