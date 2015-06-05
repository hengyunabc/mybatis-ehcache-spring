package io.github.hengyunabc.mybatis;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.ibatis.cache.Cache;

public class MybatisEhcacheFactory implements CacheFactory {

  String beanName;

  /**
   * The cache manager reference.
   */
  protected CacheManager cacheManager;

  CacheConfiguration cacheConfiguration;

  @Override
  public Cache getCache(String id) {
    if (id == null) {
      throw new IllegalArgumentException("Cache instances require an ID");
    }

    if (!cacheManager.cacheExists(id)) {
      CacheConfiguration temp = null;
      if (cacheConfiguration != null) {
        temp = cacheConfiguration.clone();
      } else {
        // based on defaultCache
        temp = cacheManager.getConfiguration().getDefaultCacheConfiguration().clone();
      }
      temp.setName(id);
      net.sf.ehcache.Cache cache = new net.sf.ehcache.Cache(temp);
      cacheManager.addCache(cache);
    }

    return new EhcacheCache(id, cacheManager.getEhcache(id));
  }

  public CacheManager getCacheManager() {
    return cacheManager;
  }

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public CacheConfiguration getCacheConfiguration() {
    return cacheConfiguration;
  }

  public void setCacheConfiguration(CacheConfiguration cacheConfiguration) {
    this.cacheConfiguration = cacheConfiguration;
  }

}
