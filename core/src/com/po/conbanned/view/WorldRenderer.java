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
import com.po.conbanned.model.Block;
import com.po.conbanned.model.Bob;
import com.po.conbanned.model.World;

public class WorldRenderer {

    private static final float GRASS_TILE_WIDTH = 1f;

    private static final float RUNNING_FRAME_DURATION = 0.06f;

    private static final float CAMERA_GRID_WIDTH = 10f;
    private static final float CAMERA_GRID_HEIGHT = 7f;
    private static final int TOOLBOX_WIDTH_PX = 50;
    private World world;
    private OrthographicCamera cam;

    /**
     * for debug rendering *
     */
    ShapeRenderer debugRenderer = new ShapeRenderer();

    /** Textures **/
    private Texture grass;

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
        System.out.println("w:" + w + ", h:" + h);
        this.width = w;
        this.fieldWidth = width - TOOLBOX_WIDTH_PX;
        this.height = h;
        ppuX = (float) fieldWidth / CAMERA_GRID_WIDTH;
        ppuY = (float) height / CAMERA_GRID_HEIGHT;
    }

    public WorldRenderer(World world) {
        this.world = world;
        this.cam = new OrthographicCamera(CAMERA_GRID_WIDTH, CAMERA_GRID_HEIGHT);
        this.cam.position.set(CAMERA_GRID_WIDTH / 2f, CAMERA_GRID_HEIGHT / 2f, 0);
        this.cam.update();
        spriteBatch = new SpriteBatch();
        loadTextures();
    }

    private void loadTextures() {
        grass = new Texture(Gdx.files.internal("images/grass.png"));

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
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawField();
        drawBlocks();
        drawBob();
/*
        drawToolbox();
 */

        spriteBatch.end();
        if (debug)
            drawDebug();
    }

    private void drawField() {
        for (float x = 0 ; x <= CAMERA_GRID_WIDTH; x+= 1 ) {
            for (float y = 0 ; y <= CAMERA_GRID_HEIGHT; y+= 1 ) {
                spriteBatch.draw(grass, x, y, 1, 1);
            }
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
        spriteBatch.draw(bobFrame, bob.getPosition().x, bob.getPosition().y, Bob.SIZE, Bob.SIZE);
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
}
