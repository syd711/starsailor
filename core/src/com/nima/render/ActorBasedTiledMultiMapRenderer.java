package com.nima.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.nima.model.Actor;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

abstract public class ActorBasedTiledMultiMapRenderer extends OrthogonalTiledMapRenderer {
  //additional rendering
  private String actorLayerName;
  private List<Actor> actorRenderers = new ArrayList<>();
  protected Actor mainActor;

  protected int actorFrameX = 0;
  protected int actorFrameY = 0;

  protected float framePixelsX;
  protected float framePixelsY;

  private int frameTilesX = 0;
  private int frameTilesY = 0;

  //variables updated for each render frame/region
  private int frameNumberX;
  private int frameNumberY;
  private TiledMap frameMap;

  private TiledMultiMapOrthographicCamera camera;

  public ActorBasedTiledMultiMapRenderer(OrthographicCamera camera, String actorLayerName, String mapFolder, String mapPrefix) {
    super(null);
    CachedTiledMap cachedTiledMap = MapCache.getInstance().initCache(mapFolder, mapPrefix);
    setMap(cachedTiledMap.getMap());

    TiledMapTileLayer groundLayer = (TiledMapTileLayer) map.getLayers().get(0);
    this.frameTilesX = groundLayer.getWidth();
    this.frameTilesY = groundLayer.getHeight();
    this.framePixelsX = groundLayer.getWidth() * groundLayer.getTileWidth() * unitScale;
    this.framePixelsY = groundLayer.getHeight() * groundLayer.getTileHeight() * unitScale;

    cachedTiledMap.renderObjects(framePixelsX, framePixelsY, unitScale);
    this.actorLayerName = actorLayerName;
    this.camera = new TiledMultiMapOrthographicCamera(this, camera);
  }

  public void setMainActor(Actor mainActor) {
    this.mainActor = mainActor;
    initMainActor();
    camera.updateCamera();
  }

  public void addActorRenderer(Actor renderer) {
    actorRenderers.add(renderer);
  }

  @Override
  public void render() {
    camera.updateCamera();
    updateActorFrames();
    MapCache.getInstance().updateCache(actorFrameX, actorFrameY);

    beginRender();
    int startX = actorFrameX - 1;
    int startY = actorFrameY - 1;

    MapLayers layers = getMap().getLayers();
    for(MapLayer layer : layers) {
      String layerName = layer.getName();

      //render layer by layer for all frames
      for(int x = startX; x < startX + 3; x++) {
        if(x >= 0) {
          for(int y = startY; y < startY + 3; y++) {
            if(y >= 0) {
              frameNumberX = x;
              frameNumberY = y;
              //get the map for the current frame
              CachedTiledMap cachedTiledMap = MapCache.getInstance().get(x, y);
              cachedTiledMap.renderObjects(framePixelsX, framePixelsY, unitScale);

              frameMap = cachedTiledMap.getMap();
              MapLayer l = frameMap.getLayers().get(layerName);

              if(l != null) {
                //simple tile map rendering
                if(l instanceof TiledMapTileLayer) {
                  renderTileLayer((TiledMapTileLayer) l);
                }
                else {
                  //check frame for collisions
                  checkCollisions(l);
                }
              }
            }
          }
        }
      }

      //additional layer checks
      if(layerName.equals(actorLayerName)) {
        for(Actor actorRenderer : actorRenderers) {
          actorRenderer.doRender();
        }
        mainActor.doRender();
      }
    } //end layer rendering

    renderGameWorld();

    endRender();

    updateGameWorld();
  }

  protected abstract void initMainActor();

  /**
   * Game specific rendering
   */
  protected abstract void renderGameWorld();

  /**
   * Post rendering, processing of game logic, ai, etc.
   */
  protected abstract void updateGameWorld();

  /**
   * Called one after the actor frame was rendered
   */
  private void checkCollisions(MapLayer layer) {
//    MapObjects objects = layer.getObjects();
//    for(MapObject object : objects) {
//      for(Actor actorRenderer : actorRenderers) {
//        if(actorRenderer.intersects(object)) {
//          System.out.println("intersected " + object);
//        }
//      }
//      if(mainActor.intersects(object)) {
//        System.out.println("intersected " + object);
//      }
//    }
  }

  @Override
  public void renderTileLayer(TiledMapTileLayer l) {
    TiledMapTileLayer layer = (TiledMapTileLayer) frameMap.getLayers().get(l.getName());

    final Color batchColor = batch.getColor();
    final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());

    final int layerWidth = layer.getWidth();
    final int layerHeight = layer.getHeight();

    final float layerTileWidth = layer.getTileWidth() * unitScale;
    final float layerTileHeight = layer.getTileHeight() * unitScale;

    float y = frameTilesY * layerTileHeight + frameNumberY*framePixelsY;
    final float[] vertices = this.vertices;

    //start rendering rows from top to bottom
    for(int row = layerHeight; row >= 0; row--) {
      float x = frameNumberX * framePixelsX;
      for(int col = 0; col < layerWidth; col++) {
        final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
        if(cell == null) {
          x += layerTileWidth;
          continue;
        }
        final TiledMapTile tile = cell.getTile();

        renderTile(color, y, vertices, x, cell, tile);
        x += layerTileWidth;
      }
      y -= layerTileHeight;
    }
  }

  private void renderTile(float color, float y, float[] vertices, float x, TiledMapTileLayer.Cell cell, TiledMapTile tile) {
    if(tile != null) {
      final boolean flipX = cell.getFlipHorizontally();
      final boolean flipY = cell.getFlipVertically();
      final int rotations = cell.getRotation();

      TextureRegion region = tile.getTextureRegion();

      float x1 = x + tile.getOffsetX() * unitScale;
      float y1 = y + tile.getOffsetY() * unitScale; //no need to add more since y is always the upper pixel value for the frame
      float x2 = x1 + region.getRegionWidth() * unitScale;
      float y2 = y1 + region.getRegionHeight() * unitScale;

      float u1 = region.getU();
      float v1 = region.getV2();
      float u2 = region.getU2();
      float v2 = region.getV();

      vertices[X1] = x1;
      vertices[Y1] = y1;
      vertices[C1] = color;
      vertices[U1] = u1;
      vertices[V1] = v1;

      vertices[X2] = x1;
      vertices[Y2] = y2;
      vertices[C2] = color;
      vertices[U2] = u1;
      vertices[V2] = v2;

      vertices[X3] = x2;
      vertices[Y3] = y2;
      vertices[C3] = color;
      vertices[U3] = u2;
      vertices[V3] = v2;

      vertices[X4] = x2;
      vertices[Y4] = y1;
      vertices[C4] = color;
      vertices[U4] = u2;
      vertices[V4] = v1;

      if(flipX) {
        float temp = vertices[U1];
        vertices[U1] = vertices[U3];
        vertices[U3] = temp;
        temp = vertices[U2];
        vertices[U2] = vertices[U4];
        vertices[U4] = temp;
      }
      if(flipY) {
        float temp = vertices[V1];
        vertices[V1] = vertices[V3];
        vertices[V3] = temp;
        temp = vertices[V2];
        vertices[V2] = vertices[V4];
        vertices[V4] = temp;
      }
      if(rotations != 0) {
        switch(rotations) {
          case TiledMapTileLayer.Cell.ROTATE_90: {
            float tempV = vertices[V1];
            vertices[V1] = vertices[V2];
            vertices[V2] = vertices[V3];
            vertices[V3] = vertices[V4];
            vertices[V4] = tempV;

            float tempU = vertices[U1];
            vertices[U1] = vertices[U2];
            vertices[U2] = vertices[U3];
            vertices[U3] = vertices[U4];
            vertices[U4] = tempU;
            break;
          }
          case TiledMapTileLayer.Cell.ROTATE_180: {
            float tempU = vertices[U1];
            vertices[U1] = vertices[U3];
            vertices[U3] = tempU;
            tempU = vertices[U2];
            vertices[U2] = vertices[U4];
            vertices[U4] = tempU;
            float tempV = vertices[V1];
            vertices[V1] = vertices[V3];
            vertices[V3] = tempV;
            tempV = vertices[V2];
            vertices[V2] = vertices[V4];
            vertices[V4] = tempV;
            break;
          }
          case TiledMapTileLayer.Cell.ROTATE_270: {
            float tempV = vertices[V1];
            vertices[V1] = vertices[V4];
            vertices[V4] = vertices[V3];
            vertices[V3] = vertices[V2];
            vertices[V2] = tempV;

            float tempU = vertices[U1];
            vertices[U1] = vertices[U4];
            vertices[U4] = vertices[U3];
            vertices[U3] = vertices[U2];
            vertices[U2] = tempU;
            break;
          }
        }
      }
      batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
    }
  }

  private void updateActorFrames() {
    float x = mainActor.getX();
    actorFrameX = (int) (x / framePixelsX);

    float y = mainActor.getY();
    actorFrameY = (int) (y / framePixelsY);
  }
}