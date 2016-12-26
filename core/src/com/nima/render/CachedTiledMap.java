package com.nima.render;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.nima.managers.EntityProperties;
import com.nima.util.PolygonUtil;
import com.nima.util.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The cached map with additional information to optimize caching
 */
public class CachedTiledMap {
  private static final Logger LOG = Logger.getLogger(CachedTiledMap.class.getName());

  private boolean rendered;
  private TiledMap map;

  private int frameNumberX;
  private int frameNumberY;

  private String filename;

  private List<MapObject> mapObjects = new ArrayList<>();

  protected CachedTiledMap(TmxCacheMapLoader loader) {
    this.map = loader.getMap();
    this.filename = loader.getFilename();
    this.frameNumberX = loader.getFrameX();
    this.frameNumberY = loader.getFrameY();
    renderObjects();
  }

  public List<MapObject> getMapObjects() {
    return mapObjects;
  }

  private void renderObjects() {
    if(!rendered) {
      rendered = true;

      float xOffset = frameNumberX * Settings.FRAME_PIXELS_X;
      float yOffset = frameNumberY * Settings.FRAME_PIXELS_Y;

      for(MapLayer mapLayer : map.getLayers()) {
        MapObjects objects = mapLayer.getObjects();
        for(MapObject object : objects) {
          renderObject(object, xOffset, yOffset);
          mapObjects.add(object);
        }
      }

      LOG.info("Rendered objects of frame " + frameNumberX + ","  + frameNumberY);
    }
  }

  /**
   * Update the position of the objects, depending
   * on the active frame.
   */
  private void renderObject(MapObject object, float xOffset, float yOffset) {
    if(object instanceof RectangleMapObject) {
      RectangleMapObject mapObject = (RectangleMapObject) object;
      Rectangle r = mapObject.getRectangle();
      r.setPosition(xOffset + r.getX(), yOffset + r.getY());

      //additional properties
      Vector2 position = r.getPosition(new Vector2());
      object.getProperties().put(EntityProperties.PROPERTY_POSITION, position);
      Vector2 centeredPosition = new Vector2(position.x+r.width/2, position.y+r.height/2);
      object.getProperties().put(EntityProperties.PROPERTY_CENTERED_POSITION, centeredPosition);
    }
    else if(object instanceof PolygonMapObject) {
      PolygonMapObject mapObject = (PolygonMapObject) object;
      Polygon polygon = mapObject.getPolygon();
      polygon.setPosition(xOffset + polygon.getX(), yOffset + polygon.getY());

      //additional properties
      Vector2 position = new Vector2(polygon.getX(), polygon.getY());
      object.getProperties().put(EntityProperties.PROPERTY_POSITION, position);
      Vector2 centeredPosition = PolygonUtil.getCenter(polygon);
      object.getProperties().put(EntityProperties.PROPERTY_CENTERED_POSITION, centeredPosition);
    }
    else if(object instanceof EllipseMapObject) {
      EllipseMapObject mapObject = (EllipseMapObject) object;
      Ellipse circle = mapObject.getEllipse();
      circle.setPosition(xOffset + circle.x, yOffset + circle.y);

      //additional properties
      Vector2 position = new Vector2(circle.x, circle.y);
      object.getProperties().put(EntityProperties.PROPERTY_POSITION, position);
      Vector2 centeredPosition = new Vector2(circle.x + circle.width / 2, circle.y + circle.height / 2);
      object.getProperties().put(EntityProperties.PROPERTY_CENTERED_POSITION, centeredPosition);
    }
  }

  public TiledMap getMap() {
    return map;
  }

  public String getFilename() {
    return filename;
  }


  @Override
  public boolean equals(Object obj) {
    return getFilename().equals(((CachedTiledMap)obj).getFilename());
  }
}
