package com.nima.actors;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.nima.components.ComponentFactory;
import com.nima.render.converters.MapConstants;

/**
 * Used for planets and stations, etc.
 */
public class Location extends AshleyEntity {
  public Location(MapObject mapObject) {
    ComponentFactory.addMapObjectComponent(this, mapObject);
    ComponentFactory.addBodyComponent(this, mapObject);
    ComponentFactory.addLocationCollisionComponent(this);
    Body body = (Body) mapObject.getProperties().get(MapConstants.PROPERTY_BOX2D_BODY);
    body.setUserData(this);
  }
}
