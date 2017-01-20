package com.starsailor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.starsailor.Game;

public class DesktopLauncher {
  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.fullscreen = false;
    config.title = "Nima 0.1";
    config.width = 1280;
    config.height = 900;
    config.resizable = false;
    config.backgroundFPS = 60;
    config.foregroundFPS = 60;
//		config.fullscreen = true;
    new LwjglApplication(new Game(), config);
//    new LwjglApplication(new Box2dTestApplication(), config);
  }
}