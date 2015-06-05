package io.github.hengyunabc.mybatis;

import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.springframework.context.ApplicationContext;

public class FacadeCache implements Cache {
  public static final String DEFAULT_CACHE_FACTORY_BEAN_NAME = "mybatisCacheDefaultFactory";
  Cache cache;

  String springContextName = null;

  String cacheFactoryBeanName = null;

  public FacadeCache(final String id) {
    String contextName =
        springContextName != null ? springContextName
            : SharedSpringContext.DEFAULT_SPRINGCONTEXT_NAME;

    ApplicationContext applicationContext = SharedSpringContext.get(contextName);
    String beanName =
        cacheFactoryBeanName != null ? cacheFactoryBeanName : DEFAULT_CACHE_FACTORY_BEAN_NAME;
    CacheFactory cacheFactory = applicationContext.getBean(beanName, CacheFactory.class);
    this.cache = cacheFactory.getCache(id);
  }

  @Override
  public String getId() {
    return cache.getId();
  }

  @Override
  public void putObject(Object key, Object value) {
    cache.putObject(key, value);
  }

  @Override
  public Object getObject(Object key) {
    return cache.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    return cache.removeObject(key);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public int getSize() {
    return cache.getSize();
  }

  @Override
  public ReadWriteLock getReadWriteLock() {
    return cache.getReadWriteLock();
  }

  public String getSpringContextName() {
    return springContextName;
  }

  public void setSpringContextName(String springContextName) {
    this.springContextName = springContextName;
  }

  public String getCacheFactoryBeanName() {
    return cacheFactoryBeanName;
  }

  public void setCacheFactoryBeanName(String cacheFactoryBeanName) {
    this.cacheFactoryBeanName = cacheFactoryBeanName;
  }
}
