package com.starsailor.actors;

import com.badlogic.gdx.math.Vector2;
import com.starsailor.components.ComponentFactory;
import com.starsailor.components.GalaxyBodyComponent;
import com.starsailor.util.box2d.Box2dUtil;

/**
 * Encapsulates the box2d body that restricts the user to this current world.
 */
public class Galaxy extends GameEntity {

  private GalaxyBodyComponent galaxyBodyComponent;
  private static Galaxy instance;

  public static Galaxy getInstance() {
    return instance;
  }

  public Galaxy() {
    instance = this;
    galaxyBodyComponent = ComponentFactory.createGalaxyBodyComponent(this);
  }

  public Vector2 getCenter() {
    return Box2dUtil.toWorldPoint(galaxyBodyComponent.body.getPosition());
  }
}
