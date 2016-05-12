package com.po.conbanned.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.po.conbanned.TheBigWheel;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "TheBigWheel";
		config.samples = 4;
		new LwjglApplication(new TheBigWheel(), config);
	}
}
