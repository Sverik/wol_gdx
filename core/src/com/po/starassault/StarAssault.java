package com.po.starassault;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.po.starassault.screens.GameScreen;

/**
 * Created by kasutaja on 13.04.15.
 */
public class StarAssault extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
