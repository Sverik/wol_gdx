package com.po.conbanned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.po.conbanned.TheBigWheel;
import com.po.conbanned.controller.DogController;
import com.po.conbanned.controller.HoverController;
import com.po.conbanned.controller.MapController;
import com.po.conbanned.controller.SheepController;
import com.po.conbanned.controller.WireController;
import com.po.conbanned.model.World;
import com.po.conbanned.view.WorldRenderer;

public class GameScreen implements Screen, InputProcessor {

	private TheBigWheel game;
	private World world;
	private WorldRenderer renderer;
	private WireController wireController;
	private DogController dogController;
	private SheepController sheepController;
	private HoverController hoverController;
	private MapController mapController;

	private int width, height;

	public GameScreen(TheBigWheel conBanned) {
		this.game = conBanned;
	}

	@Override
	public void show() {
		world = new World();
		renderer = new WorldRenderer(world);
		wireController = new WireController(world);
		dogController = new DogController(world);
		sheepController = new SheepController(world);
		hoverController = new HoverController(world, renderer);
		mapController = new MapController(world);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) {
		mapController.update(delta);
		wireController.update(delta);
		dogController.update(delta);
		sheepController.update(delta);
		world.physics.step(delta, 6, 2);
		sheepController.afterPhysics(delta);
		boolean running = true;
		if (!running) {
			game.setScreen(new GameOverScreen(game, 0));
		}
		renderer.render();
	}

	@Override
	public void resize(int width, int height) {
		renderer.setSize(width, height);
		this.width = width;
		this.height = height;
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
	}

	// * InputProcessor methods ***************************//

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
		if ('f' == character || 'F' == character) {
			hoverController.showFlock(lastMouseX, lastMouseY);
		}
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		hoverController.down(x, y);
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		hoverController.up(x, y);
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		hoverController.down(x, y);
		return true;
	}

	int lastMouseX = 0;
	int lastMouseY = 0;

	@Override
	public boolean mouseMoved(int x, int y) {
		lastMouseX = x;
		lastMouseY = y;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
