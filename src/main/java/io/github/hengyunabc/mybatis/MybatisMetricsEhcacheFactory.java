package io.github.hengyunabc.mybatis;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.ibatis.cache.Cache;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ehcache.InstrumentedEhcache;

public class MybatisMetricsEhcacheFactory extends MybatisEhcacheFactory {
  MetricRegistry registry;

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
      Ehcache instrumentCache = InstrumentedEhcache.instrument(registry, cache);
      cacheManager.addCache(instrumentCache);
    }
    return new EhcacheCache(id, cacheManager.getEhcache(id));
  }

  public MetricRegistry getRegistry() {
    return registry;
  }

  public void setRegistry(MetricRegistry registry) {
    this.registry = registry;
  }
}
