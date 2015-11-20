package com.po.conbanned;

import com.badlogic.gdx.Game;
import com.po.conbanned.screens.GameScreen;

public class TheBigWheel extends Game {
	@Override
	public void create() {
		setScreen(new GameScreen(this));
	}
}
