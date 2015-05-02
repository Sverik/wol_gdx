package com.po.conbanned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.po.conbanned.ConBanned;

public class GameOverScreen implements Screen, InputProcessor {
	ConBanned game;

	private int width, height;
	private SpriteBatch batch;
	private BitmapFont fontNormal = new BitmapFont();
	private BitmapFont fontToContinue = new BitmapFont();
	private float fontScale = -1f;
	private int killCount;

	public GameOverScreen(ConBanned game, int killCount) {
		this.game = game;
		this.killCount = killCount;
		batch = new SpriteBatch();
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
		if (timer <= 0) {
			game.setScreen(new GameScreen(game));
			return true;
		}
		return false;
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

	float timer = 2f;

	@Override
	public void render(float delta) {
		timer -= delta;
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glViewport(0, 0, width, height);
		batch.begin();

		if (fontScale <= 0) {
			findScale();
		}

		fontNormal.drawMultiLine(batch, getText(), 20, height - 60);

		if (timer <= 0) {
			fontToContinue.draw(batch, "Click/Tap to start again.", 20, 30);
		}
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
		return killCount > 0 ?
				"Your palace was overrun!\n" +
						"Next stop, International Criminal Tribunal!\n" +
						"Among other crimes, you are charged with using\n" +
						"anti-personnel mines to fend off protesters.\n" +
						"Killing " + killCount + " of them\n" +
						"does not make your case in court any better."
				:
				"Your palace was overrun!\n" +
						"It looks like your days of ruling are over,\n" +
						"but at least you did not resort to\n" +
						"using landmines against your own people.\n\n" +
						"Losing gracefully, that is the hardest thing."
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
