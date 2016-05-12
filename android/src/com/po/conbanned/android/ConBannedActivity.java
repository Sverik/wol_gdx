package com.po.conbanned.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.po.conbanned.TheBigWheel;

public class ConBannedActivity extends AndroidApplication {
	static {
		GdxNativesLoader.load();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;
		config.useGLSurfaceView20API18 = true;
		initialize(new TheBigWheel(), config);
	}
}
