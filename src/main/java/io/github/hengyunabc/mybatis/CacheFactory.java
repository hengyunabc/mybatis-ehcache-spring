package io.github.hengyunabc.mybatis;

import org.apache.ibatis.cache.Cache;

public interface CacheFactory {
  public Cache getCache(String id);
}
