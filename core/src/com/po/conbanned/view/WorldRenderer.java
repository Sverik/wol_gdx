package com.po.conbanned.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.utils.Array;
import com.po.conbanned.model.Dog;
import com.po.conbanned.model.Obstacle;
import com.po.conbanned.model.Reference;
import com.po.conbanned.model.Sheep;
import com.po.conbanned.model.World;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class WorldRenderer {

	private static final float DEBUG_CAM_HEIGHT = 500f;

	private static final float GRASS_TILE_WIDTH = 5f;

	private static final float TARGET_FIELD_RATIO = (float) World.GRID_WIDTH / World.GRID_HEIGHT;

	private static final Color HOVER_COLOR = new Color(1f, 1f, 1f, 0.7f);
	private static final Color HOVER_NP_COLOR = new Color(1f, 1f, 1f, 0.2f);

	private World world;
	private OrthographicCamera cam;

	/**
	 * for debug rendering *
	 */
	private OrthographicCamera debugCam;
	ShapeRenderer debugRenderer = new ShapeRenderer();
	SpriteBatch debugTextRenderer = new SpriteBatch();
	BitmapFont debugFont;
	FatArcRenderer fatArcRenderer;

	/**
	 * Textures *
	 */
	private Texture lammas;

	private SpriteBatch spriteBatch;
	private int width;
	private int height;
	private int fieldHeight;
	private int fieldWidth;

	public void setSize(int w, int h) {
		width = w;
		height = h;
		float ratio = (float) width / (float) height;
		if (ratio > TARGET_FIELD_RATIO) {
			fieldHeight = height;
			fieldWidth = (int) Math.floor(fieldHeight * TARGET_FIELD_RATIO);
		} else {
			fieldWidth = width;
			fieldHeight = (int) Math.floor(fieldWidth / TARGET_FIELD_RATIO);
		}
		System.out.format("rW = %d rH = %d r = %f%n", width, height, ratio);
		System.out.format("fW = %d fH = %d R = %f%n", fieldWidth, fieldHeight, (float) fieldWidth / (float) fieldHeight);
		System.out.format("---%n");

		debugCam.viewportWidth = (float) width / height * DEBUG_CAM_HEIGHT;
		debugCam.position.set(debugCam.viewportWidth / 2, DEBUG_CAM_HEIGHT / 2, 0);
		debugCam.update();
		debugFont.setScale(1, 1);
	}

	public WorldRenderer(World world) {
		this.fatArcRenderer = new FatArcRenderer(world);
		this.world = world;
		this.cam = new OrthographicCamera(World.GRID_WIDTH, World.GRID_HEIGHT);
		this.cam.position.set(World.GRID_WIDTH / 2f, World.GRID_HEIGHT / 2f, 0);
		this.cam.update();

		debugCam = new OrthographicCamera(DEBUG_CAM_HEIGHT, DEBUG_CAM_HEIGHT);
		debugCam.update();

		spriteBatch = new SpriteBatch();
		loadTextures();
	}

	private void loadTextures() {
		lammas = new Texture(Gdx.files.internal("images/lammas.png"));
		lammas.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Regular.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 16;
		debugFont = generator.generateFont(parameter);
		generator.dispose();
	}

	public void render() {
		Gdx.gl.glClearColor(0.1f, 0.2f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//Gdx.gl.glViewport((width - fieldWidth) / 2, 0, fieldWidth, fieldHeight);
		Gdx.gl.glViewport(0, 0, fieldWidth, fieldHeight);

		cam.position.set(World.GRID_WIDTH / 2f, World.GRID_HEIGHT / 2f + world.trip, 0);
		cam.update();

		spriteBatch.setProjectionMatrix(cam.combined);
		spriteBatch.begin();
		drawSheep();
		spriteBatch.end();

		drawDebug();

	}

	private void drawSheep() {
		Color color = new Color(0f, 0f, 0f, 1f);
		float colorStep = 1f / world.getSheep().size();
		for (Sheep s : world.getSheep()) {
			spriteBatch.setColor(color);
			spriteBatch.draw(lammas, s.getPosition().x - Sheep.RADIUS, s.getPosition().y - Sheep.RADIUS, Sheep.RADIUS, Sheep.RADIUS, Sheep.RADIUS * 2, Sheep.RADIUS * 2, 1,
					1, s.getOrientation().angle(), 0, 0, lammas.getWidth(), lammas.getHeight(), false, false);
			color.add(colorStep, colorStep, colorStep, 0);
		}
		color = new Color(1f, 1f, 1f, 1f);
		spriteBatch.setColor(color);
	}

	private void drawDebug() {
		debugRenderer.setProjectionMatrix(cam.combined);
		fatArcRenderer.setProjectionMatrix(cam.combined);

		// Trace
/**/
		debugRenderer.begin(ShapeType.Filled);
		for (Vector3 trace : world.getDogTrace()) {
			debugRenderer.setColor(trace.z, 0, 0, 1f);
			debugRenderer.circle(trace.x, trace.y, trace.z);
		}
		debugRenderer.end();
/**/

		// Sheep
		debugRenderer.begin(ShapeType.Line);
		debugRenderer.setColor(new Color(0.3f, 0.3f, 1, 1));
		for (Sheep sheep : world.getSheep()) {
			drawDebugSheep(sheep);
		}
		debugRenderer.end();

		// Obstacles
		debugRenderer.begin(ShapeType.Filled);
		debugRenderer.setColor(new Color(0.7f, 0.7f, 0.7f, 1));
		for (Obstacle obstacle : world.getObstacles()) {
			Vector2 pos = obstacle.getBody().getPosition();
			Array<Fixture> fixtures = obstacle.getBody().getFixtureList();
			for (Fixture fix : fixtures) {
				PolygonShape shape = (PolygonShape) fix.getShape();
				Vector2 vertex0 = new Vector2();
				Vector2 vertex1 = new Vector2();
				shape.getVertex(0, vertex0);
				for (int i = 1; i < shape.getVertexCount() + 1; i++) {
					shape.getVertex(i % shape.getVertexCount(), vertex1);
					debugRenderer.line(vertex0.x + pos.x, vertex0.y + pos.y, vertex1.x + pos.x, vertex1.y + pos.y);
					vertex0.set(vertex1);
				}
//                debugRenderer.rect(, 0, World.GRID_WIDTH / 2 - 1f, rect.height);
			}
		}
		debugRenderer.end();

		// Touch
		debugRenderer.begin(ShapeType.Filled);
		debugRenderer.setColor(new Color(0f, (world.getHoverState() == World.HoverState.NONE ? 0.4f : 1f), 0, 1f));
		debugRenderer.circle(world.getHover().x, world.getHover().y, 0.5f);
		debugRenderer.end();

		// Dog
		Dog dog = world.getDog();
		float radius = Dog.RADIUS * 20;
		fatArcRenderer.begin(FatArcRenderer.ShapeType.Line);
		fatArcRenderer.setColor(1, 0, 0, 1);
		fatArcRenderer.arc(dog.getPosition().x, dog.getPosition().y, radius, 20f, 80f, 3);
//		debugRenderer.po
//		debugRenderer.circle(dog.getPosition().x, dog.getPosition().y, radius, 12);
		dog.getOrientation().nor().scl(radius);
//		debugRenderer.line(dog.getPosition(), new Vector2(dog.getPosition()).add(dog.getOrientation()));
		fatArcRenderer.end();

		fatArcRenderer.begin(FatArcRenderer.ShapeType.Filled);
		fatArcRenderer.setColor(1, 1, 0, 1);
		fatArcRenderer.arc(dog.getPosition().x, dog.getPosition().y, radius + 3, radius + 10, dog.getOrientation().angle(), 80f, 7);
		fatArcRenderer.end();

		// Sheep flock
		debugFlock();

		drawDebugText();
	}

	private void debugFlock() {
		if (world.debugRequest == World.DebugRequest.FLOCK) {
			world.debugRequest = null;
			world.physics.QueryAABB(new QueryCallback() {
				@Override
				public boolean reportFixture(Fixture fixture) {
					if (fixture.getUserData() != null && (fixture.getUserData() instanceof Reference)) {
						Reference ref = ((Reference) fixture.getUserData());
						if (ref.getType() == Reference.Type.SHEEP) {
							world.showFlock = ref.getSheep();
							return false;
						}
					}
					return true;
				}
			}, world.debugCoords.x, world.debugCoords.y, world.debugCoords.x, world.debugCoords.y);
		}

		if (world.showFlock != null) {
			debugRenderer.begin(ShapeType.Filled);
			debugRenderer.setColor(new Color(1, 0.3f, 0.3f, 0.0f));
			debugRenderer.circle(world.showFlock.getPosition().x, world.showFlock.getPosition().y, 1.5f);
			debugRenderer.setColor(new Color(0.2f, 0.2f, 1f, 0.0f));
			for (Sheep inFlock : world.showFlock.getFlock()) {
				debugRenderer.circle(inFlock.getPosition().x, inFlock.getPosition().y, 1f);
			}
			debugRenderer.end();

			debugRenderer.begin(ShapeType.Line);
			debugRenderer.setColor(new Color(0.2f, 0.2f, 1f, 0.0f));
			debugRenderer.circle(world.showFlock.getPosition().x, world.showFlock.getPosition().y, Sheep.FLOCK_RADIUS);
			debugRenderer.end();
		}

	}

	private void drawDebugSheep(Sheep sheep) {
		debugRenderer.setColor(new Color(0.3f, 0.3f, 1, 1));
		debugRenderer.circle(sheep.getPosition().x, sheep.getPosition().y, Sheep.RADIUS, 12);
		sheep.getOrientation().nor().scl(Sheep.RADIUS);
        debugRenderer.line(sheep.getPosition(), new Vector2(sheep.getPosition()).add(sheep.getOrientation()));
//		debugRenderer.line(sheep.getPosition(), new Vector2(sheep.getPosition()).add(sheep.getDesiredMovement()));
		debugRenderer.setColor(1, 0.2f, 0.2f, 1);
//		debugRenderer.line(sheep.getPosition(), new Vector2(sheep.getPosition()).add(sheep.dbgDogEffect));
		debugRenderer.setColor(0.2f, 1, 0.2f, 1);
//		debugRenderer.line(sheep.getPosition(), new Vector2(sheep.getPosition()).add(sheep.dbgFlockCenterEffect));
		debugRenderer.setColor(0.2f, 1, 1, 1);
//		debugRenderer.line(sheep.getPosition(), new Vector2(sheep.getPosition()).add(sheep.dbgFlockAlignmentEffect));
	}

	private void drawDebugText() {
		Gdx.gl.glViewport(0, 0, width, height);

		debugTextRenderer.setProjectionMatrix(debugCam.combined);
		debugTextRenderer.begin();
//		debugTextRenderer.draw(lammas, 90, 20, 10, 10);

		float lineHeight = debugFont.getLineHeight();
		float y = DEBUG_CAM_HEIGHT - 12;
		for (String line : world.getDebug()) {
			debugFont.draw(debugTextRenderer, line, 12, y);
			y -= lineHeight;
		}
		world.clearDebug();
		debugTextRenderer.end();
	}

	public Vector2 screenToTile(int sx, int sy, Vector2 target) {
		Vector3 tc = cam.unproject(new Vector3(sx, sy, 0), 0, 0, fieldWidth, fieldHeight);
		tc.x = (float) Math.floor(tc.x);
		tc.y = (float) Math.floor(tc.y);
		target.set(tc.x, tc.y);
		return target;
	}

}
