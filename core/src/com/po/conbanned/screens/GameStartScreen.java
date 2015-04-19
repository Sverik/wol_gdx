package com.po.conbanned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.po.conbanned.ConBanned;
import com.po.conbanned.model.World;

public class GameStartScreen implements Screen, InputProcessor {
    ConBanned game;

    private int width, height;
    private SpriteBatch batch;
    private BitmapFont fontNormal;
    private BitmapFont fontToContinue;
    private float fontScale = -1f;

    public GameStartScreen(ConBanned game) {
        this.game = game;
        batch = new SpriteBatch();
        fontNormal = new BitmapFont();
        fontToContinue = new BitmapFont();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        game.setScreen(new GameScreen(game));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glViewport(0, 0, width, height);

        batch.begin();

        if (fontScale <= 0) {
            findScale();
        }

        fontNormal.drawMultiLine(batch, getText(), 20, height - 60);

        fontToContinue.draw(batch, "Click/Tap to start.", 20, 30);
        batch.end();

    }

    private void findScale() {
        String text = getText();
        fontScale = 0.01f;
        while (true) {
            if (fontNormal.getMultiLineBounds(text).width > width - 40) {
                break;
            }
            fontScale += 0.02f;
            fontNormal.setScale(fontScale);
        }

    }

    private String getText() {
        return
                "Looks like your last days as a dictator have come.\n\n" +
                "Protesters are coming and their plans are bigger than just having tea with you.\n" +
                "You have a huge stockpile of landmines,\n" +
                "and regardless of the Anti-Personnel Mine Ban Convention that you as a president signed,\n" +
                "it seems to be your last option for defending yourself against the angry mob.\n\n" +
                "Landmines can be placed by clicking/tapping on the field.\n" +
                "When " + World.MAX_HQ_HEALTH + " protesters manage to enter your palace,\n" +
                "it will be all over.\n\n\n" +
                "(Game made in 48 hours for Ludum Dare 32 competition.)"
                ;
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
