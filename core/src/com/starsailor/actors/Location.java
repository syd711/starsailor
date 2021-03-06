package com.starsailor.actors;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.starsailor.components.ComponentFactory;
import com.starsailor.render.converters.MapConstants;

/**
 * Used for planets and stations, etc.
 */
public class Location extends GameEntity {

  private MapObject mapObject;

  public Location(MapObject mapObject) {
    this.mapObject = mapObject;

    ComponentFactory.addMapObjectComponent(this, mapObject);
    ComponentFactory.addBodyComponent(this, mapObject);
    Body body = (Body) mapObject.getProperties().get(MapConstants.PROPERTY_BOX2D_BODY);
    body.setUserData(this);
  }

  @Override
  public String toString() {
    return "Location '" + mapObject.getName() + "'";
  }
}
