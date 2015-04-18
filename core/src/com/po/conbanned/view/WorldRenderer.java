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

    private static final float RUNNING_FRAME_DURATION = 0.06f;

    private static final int TOOLBOX_WIDTH_PX = 50;
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

    private TextureRegion bobIdleLeft;
    private TextureRegion bobIdleRight;
    private TextureRegion blockTexture;
    private TextureRegion bobFrame;
    private TextureRegion bobJumpLeft;
    private TextureRegion bobFallLeft;
    private TextureRegion bobJumpRight;
    private TextureRegion bobFallRight;

    /** Animations **/
    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;

    private SpriteBatch spriteBatch;
    private boolean debug = true;
    private int width;
    private int fieldWidth;
    private int height;
    private float ppuX;	// pixels per unit on the X axis
    private float ppuY;	// pixels per unit on the Y axis

    public void setSize (int w, int h) {
        this.width = w;
        this.fieldWidth = width - TOOLBOX_WIDTH_PX;
        this.height = h;
        ppuX = (float) fieldWidth / World.GRID_WIDTH;
        ppuY = (float) height / World.GRID_HEIGHT;
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

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("images/textures/textures.pack"));
        bobIdleLeft = atlas.findRegion("bob-01");
        bobIdleRight = new TextureRegion(bobIdleLeft);
        bobIdleRight.flip(true, false);
        blockTexture = atlas.findRegion("block");
        bobJumpLeft = atlas.findRegion("bob-up");
        bobJumpRight = new TextureRegion(bobJumpLeft);
        bobJumpRight.flip(true, false);
        bobFallLeft = atlas.findRegion("bob-down");
        bobFallRight = new TextureRegion(bobFallLeft);
        bobFallRight.flip(true, false);
        TextureRegion[] walkLeftFrames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            walkLeftFrames[i] = atlas.findRegion("bob-0" + (i + 2));
        }
        walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);

        TextureRegion[] walkRightFrames = new TextureRegion[5];

        for (int i = 0; i < 5; i++) {
            walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
            walkRightFrames[i].flip(true, false);
        }
        walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, walkRightFrames);
    }

    public void render() {
        Gdx.gl.glViewport(0, 0, fieldWidth, height);
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawField();
        drawLandmines();
        drawAttackers();
        drawHover();
/*
        drawBlocks();
        drawBob();
*/
        spriteBatch.end();
/*
        if (debug)
            drawDebug();
*/

        drawToolbox();

    }

    private void drawToolbox() {
        Gdx.gl.glViewport(fieldWidth, 0, width-fieldWidth, height);

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
        for (Attacker a : world.getAttackers()) {
            spriteBatch.draw(rev, a.getPosition().x, a.getPosition().y, Attacker.SIZE / 2, Attacker.SIZE / 2, Attacker.SIZE, Attacker.SIZE, 1,
                    1, a.getVelocity().angle(), 0, 0, rev.getWidth(), rev.getHeight(), false, false);
        }
    }

    private void drawHover() {
        if (world.getHoverState() == World.HoverState.PLACING_LANDMINE) {
            spriteBatch.draw(this.mine, world.getHover().x, world.getHover().y, Landmine.SIZE, Landmine.SIZE);
        }
    }

    private void drawBlocks() {
        for (Block block : world.getBlocks()) {
            spriteBatch.draw(blockTexture, block.getPosition().x, block.getPosition().y, Block.SIZE, Block.SIZE);
        }
    }

    private void drawBob() {
        Bob bob = world.getBob();
        bobFrame = bob.isFacingLeft() ? bobIdleLeft : bobIdleRight;
        if(bob.getState().equals(Bob.State.WALKING)) {
            bobFrame = bob.isFacingLeft() ? walkLeftAnimation.getKeyFrame(bob.getStateTime(), true) : walkRightAnimation.getKeyFrame(bob.getStateTime(), true);
        } else if (bob.getState().equals(Bob.State.JUMPING)) {
            if (bob.getVelocity().y > 0) {
                bobFrame = bob.isFacingLeft() ? bobJumpLeft : bobJumpRight;
            } else {
                bobFrame = bob.isFacingLeft() ? bobFallLeft : bobFallRight;
            }
        }
        spriteBatch.draw(rev, bob.getPosition().x, bob.getPosition().y, Bob.SIZE, Bob.SIZE);
    }

    private void drawDebug() {
        // render blocks
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.begin(ShapeType.Line);
        for (Block block : world.getBlocks()) {
            Rectangle rect = block.getBounds();
            debugRenderer.setColor(new Color(1, 0, 0, 1));
            debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }
        // render Bob
        Bob bob = world.getBob();
        Rectangle rect = bob.getBounds();
        debugRenderer.setColor(new Color(0, 1, 0, 1));
        debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
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
