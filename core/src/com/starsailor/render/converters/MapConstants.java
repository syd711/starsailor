package com.starsailor.render.converters;

/**
 * Constants used in the Tiled Map Editor
 */
public class MapConstants {

  public static final String PROPERTY_OBJECT_TYPE = "type";

  public static final String PROPERTY_DOCKABLE = "dockable";
  public static final String PROPERTY_DOCK_TIME = "dockTime";
  public static final String PROPERTY_FRACTION = "fraction";

  //entity properties
  public static final String PROPERTY_POSITION = "position";
  public static final String PROPERTY_CENTERED_POSITION = "centeredPosition";

  //light properties
  public static final String PROPERTY_LIGHT_DISTANCE = "lightDistance";
  public static final String PROPERTY_LIGHT_DEGREE = "lightDegree";
  public static final String PROPERTY_CONE_DEGREE = "coneDegree";

  //used to store the body of an entity temporary to the map object
  public static final String PROPERTY_BOX2D_BODY = "body";

  //route point position
  public static final String PROPERTY_INDEX = "index";

  // PointLight, DirectionalLight, PositionalLight, ConeLight
  public static final String TYPE_CONE_LIGHT = "ConeLight";
  public static final String TYPE_POINT_LIGHT = "PointLight";
  public static final String TYPE_ROUTE = "Route";

  //TODO fix this
  @Deprecated
  public static final String TYPE_PLANET = "Planet";
  public static final String TYPE_STATION = "Station";


}
