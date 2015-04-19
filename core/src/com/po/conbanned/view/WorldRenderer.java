package com.po.conbanned.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.po.conbanned.model.Attacker;
import com.po.conbanned.model.Block;
import com.po.conbanned.model.Bob;
import com.po.conbanned.model.Landmine;
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
    private Texture grass;
    private Texture rev;
    private Texture mine;
    private Texture hq;

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
        grass = new Texture(Gdx.files.internal("images/grass.png"));
        rev = new Texture(Gdx.files.internal("images/rev-8px.png"));
        mine = new Texture(Gdx.files.internal("images/mine-8px.png"));
        hq = new Texture(Gdx.files.internal("images/hq-199px.png"));
    }

    public void render() {
        Gdx.gl.glViewport(0, 0, fieldWidth, height);
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawField();
        drawLandmines();
        drawAttackers();
        drawHq();
        drawHover();
        spriteBatch.end();

        drawHqHealth();
    }

    private void drawField() {
        for (float x = 0 ; x <= World.GRID_WIDTH; x+= GRASS_TILE_WIDTH ) {
            for (float y = 0 ; y <= World.GRID_HEIGHT; y+= GRASS_TILE_WIDTH ) {
                spriteBatch.draw(grass, x, y, GRASS_TILE_WIDTH, GRASS_TILE_WIDTH);
            }
        }
    }

    private void drawLandmines() {
        for (Landmine mine : world.getLandmines()) {
            spriteBatch.draw(this.mine, mine.getPosition().x, mine.getPosition().y, Landmine.SIZE, Landmine.SIZE);
        }
    }

    private void drawAttackers() {
        Color color = new Color(1f, 1f, 1f, 1f);
        for (Attacker a : world.getAttackers()) {
            if (a.getState() == Attacker.State.ALIVE) {
                color.a = 1f;
            } else if (a.getState() == Attacker.State.DEAD) {
                color.a = Math.max(0f, a.getSpinningTimeLeft() / Attacker.MAX_SPINNING_TIME);
            }
            spriteBatch.setColor(color);
            spriteBatch.draw(rev, a.getPosition().x, a.getPosition().y, Attacker.SIZE / 2, Attacker.SIZE / 2, Attacker.SIZE, Attacker.SIZE, 1,
                    1, a.getVelocity().angle(), 0, 0, rev.getWidth(), rev.getHeight(), false, false);
        }
        color = new Color(1f, 1f, 1f, 1f);
        spriteBatch.setColor(color);
    }

    private void drawHq() {
        spriteBatch.draw(hq, world.targetHouse.x, world.targetHouse.y, world.targetHouse.width, world.targetHouse.height);
    }

    private void drawHover() {
        Color hc = HOVER_NP_COLOR;
        switch (world.getHoverState()) {
            case NONE:
                break;
            case PLACING_LANDMINE:
                hc = HOVER_COLOR;
            case PLACING_LANDMINE_NP:
                Color color = spriteBatch.getColor();
                spriteBatch.setColor(hc);
                spriteBatch.draw(this.mine, world.getHover().x, world.getHover().y, Landmine.SIZE, Landmine.SIZE);
                spriteBatch.setColor(color);
                break;
        }
    }

    private void drawHqHealth() {
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.begin(ShapeType.Filled);
        float x = world.targetHouse.x + 1f;
        float y = world.targetHouse.y - 0.3f;
        float height = 1f;
        float fullWidth = world.targetHouse.width - 2f;
        debugRenderer.setColor(new Color(1, 0, 0, 1));
        debugRenderer.rect(x, y, fullWidth, height);
        debugRenderer.setColor(new Color(0, 1, 0, 1));
        debugRenderer.rect(x, y, fullWidth * world.hqHealth / world.MAX_HQ_HEALTH, height);
        debugRenderer.end();
    }

    public Vector2 screenToTile(int sx, int sy, Vector2 target) {
        Vector3 tc = cam.unproject(new Vector3(sx, sy, 0), 0, 0, fieldWidth, height);
        tc.x = (float) Math.floor(tc.x);
        tc.y = (float) Math.floor(tc.y);
        target.set(tc.x, tc.y);
        return target;
    }
}
