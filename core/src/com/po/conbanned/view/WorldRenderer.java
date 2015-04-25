package com.po.conbanned.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.utils.Array;
import com.po.conbanned.model.Dog;
import com.po.conbanned.model.Obstacle;
import com.po.conbanned.model.Sheep;
import com.po.conbanned.model.World;

public class WorldRenderer {

    private static final float GRASS_TILE_WIDTH = 5f;

    private static final int TOOLBOX_WIDTH_PX = 0;

    private static final Color HOVER_COLOR = new Color(1f, 1f, 1f, 0.7f);
    private static final Color HOVER_NP_COLOR = new Color(1f, 1f, 1f, 0.2f);

    private World world;
    private OrthographicCamera cam;

    /**
     * for debug rendering *
     */
    ShapeRenderer debugRenderer = new ShapeRenderer();

    /** Textures **/
    private Texture lammas;

    private SpriteBatch spriteBatch;
    private int width;
    private int fieldWidth;
    private int height;

    public void setSize (int w, int h) {
        this.width = w;
        this.fieldWidth = width - TOOLBOX_WIDTH_PX;
        this.height = h;
    }

    public WorldRenderer(World world) {
        this.world = world;
        this.cam = new OrthographicCamera(World.GRID_WIDTH, World.GRID_HEIGHT);
        this.cam.position.set(World.GRID_WIDTH / 2f, World.GRID_HEIGHT / 2f, 0);
        this.cam.update();
        spriteBatch = new SpriteBatch();
        loadTextures();
    }

    private void loadTextures() {
        lammas = new Texture(Gdx.files.internal("images/lammas.png"));
        lammas.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.2f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glViewport(0, 0, fieldWidth, height);

        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawSheep();
        spriteBatch.end();

        drawDebug();
    }

    private void drawSheep() {
        Color color = new Color(1f, 1f, 1f, 1f);
        for (Sheep s : world.getSheep()) {
            spriteBatch.setColor(color);
            spriteBatch.draw(lammas, s.getPosition().x - Sheep.RADIUS, s.getPosition().y - Sheep.RADIUS, Sheep.RADIUS, Sheep.RADIUS, Sheep.RADIUS * 2, Sheep.RADIUS * 2, 1,
                    1, s.getOrientation().angle(), 0, 0, lammas.getWidth(), lammas.getHeight(), false, false);
        }
        color = new Color(1f, 1f, 1f, 1f);
        spriteBatch.setColor(color);
    }

    private void drawDebug() {
        debugRenderer.setProjectionMatrix(cam.combined);

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
        debugRenderer.begin(ShapeType.Line);
        debugRenderer.setColor(new Color(0.7f, 0.7f, 0.7f, 1));
        for (Obstacle obstacle : world.getObstacles()) {
            Rectangle rect = obstacle.getShape();
//            debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            Vector2 pos = obstacle.getBody().getPosition();
            Array<Fixture> fixtures = obstacle.getBody().getFixtureList();
            for (Fixture fix : fixtures) {
                PolygonShape shape = (PolygonShape) fix.getShape();
                Vector2 vertex0 = new Vector2();
                Vector2 vertex1 = new Vector2();
                shape.getVertex(0, vertex0);
                for (int i = 1 ; i < shape.getVertexCount() + 1; i++) {
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
        float radius = 2f;
        debugRenderer.begin(ShapeType.Line);
        debugRenderer.setColor(new Color(1, 0, 0, 1));
        debugRenderer.circle(dog.getPosition().x, dog.getPosition().y, radius, 12);
        dog.getOrientation().nor().scl(radius);
        debugRenderer.line(dog.getPosition(), new Vector2(dog.getPosition()).add(dog.getOrientation()));
        debugRenderer.end();

        // Sheep flock
        final Sheep[] reported = new Sheep[1];
        world.physics.QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                if (fixture.getUserData() != null && (fixture.getUserData() instanceof Sheep)) {
                    reported[0] = (Sheep) fixture.getUserData();
                    return false;
                }
                return true;
            }
        }, world.getHover().x, world.getHover().y, world.getHover().x, world.getHover().y);
        debugRenderer.begin(ShapeType.Filled);
        debugRenderer.setColor(new Color(0.2f, 0.2f, 1f, 0.0f));
        if (reported[0] != null) {
            for (Sheep inFlock : reported[0].getFlock()) {
                debugRenderer.circle(inFlock.getPosition().x, inFlock.getPosition().y, 1.5f);
            }
        }
        debugRenderer.end();

    }
    
    private void drawDebugSheep(Sheep sheep) {
        final float radius = Sheep.RADIUS;
//        debugRenderer.circle(sheep.getPosition().x, sheep.getPosition().y, radius, 12);
        sheep.getOrientation().nor().scl(radius);
//        debugRenderer.line(sheep.getPosition(), new Vector2(sheep.getPosition()).add(sheep.getOrientation()));
//        debugRenderer.line(sheep.getPosition(), new Vector2(sheep.getPosition()).add(sheep.getDesiredMovement()));
    }

    public Vector2 screenToTile(int sx, int sy, Vector2 target) {
        Vector3 tc = cam.unproject(new Vector3(sx, sy, 0), 0, 0, fieldWidth, height);
        tc.x = (float) Math.floor(tc.x);
        tc.y = (float) Math.floor(tc.y);
        target.set(tc.x, tc.y);
        return target;
    }

}
