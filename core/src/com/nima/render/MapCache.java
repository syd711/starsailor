package com.nima.render;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

/**
 * Used to cache a loaded map.
 */
public class MapCache {
  private static final Logger LOG = Logger.getLogger(MapCache.class.getName());
  private static MapCache INSTANCE = new MapCache();

  protected LinkedHashMap<String, CachedTiledMap> cacheMap = new LinkedHashMap<>();

  //file infos
  private String folder;
  private String prefix;

  //center position
  private int frameX = -1;
  private int frameY = -1;


  public static MapCache getInstance() {
    return INSTANCE;
  }

  //force singleton
  private MapCache() {
    //nothing
  }

  public CachedTiledMap initCache(String folder, String filePrefix) {
    this.folder = folder;
    this.prefix = filePrefix;

    CachedMapLoader cachedMapLoader = new CachedMapLoader(this, 0, 0);
    cachedMapLoader.setGdlThread(true);
    cachedMapLoader.run();

    String key = keyFor(0, 0);
    return cacheMap.get(key);
  }

  /**
   * Invokes the map loader thread for the given position.
   * If the current frame hasn't changed, the loader thread is not notified.
   * @param frameX current frame X
   * @param frameY current frame Y
   */
  public void updateCache(int frameX, int frameY) {
    if(this.frameX != frameX || this.frameY != frameY) {
      this.frameX = frameX;
      this.frameY = frameY;
      new CachedMapLoader(this, frameX, frameY).start();
    }

    evictCache();
  }

  /**
   * Checks which maps are not used anymore
   */
  private void evictCache() {
    //TODO cache eviction
//    while(cacheMap.size() > CACHE_SIZE) {
//      Iterator<Map.Entry<String, TiledMap>> iterator = cacheMap.entrySet().iterator();
//      Map.Entry<String, TiledMap> next = iterator.next();
//      iterator.remove();
//      LOG.info("Evicted " + next.getKey());
//    }
  }

  /**
   * Returns the given frame tile from the cache.
   * If the frame is not found, an exception is thrown since
   * this method is access from the rendering and should never
   * load a map directly.
   * @param x frame position X
   * @param y frame position Y
   */
  public CachedTiledMap get(int x, int y) {
    String key = keyFor(x, y);
    if(cacheMap.containsKey(key)) {
      return cacheMap.get(key);
    }

    throw new UnsupportedOperationException("Tile " + x + ", " + y + " not cached yet.");
  }

  /**
   * Helper for generating the cache key
   */
  protected String keyFor(int x, int y) {
    String key = prefix + x + "," + y + ".tmx";
    if(folder != null) {
      if(!folder.endsWith("/")) {
        folder+="/";
      }
      key = folder + key;
    }
    return key;
  }

}
