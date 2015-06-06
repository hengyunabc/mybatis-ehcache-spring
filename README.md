# mybatis

## Why we need this project
The official mybatis-ehcache is very simple. It create ehcache like follow:
```java
protected static CacheManager CACHE_MANAGER = CacheManager.create();
```
User can not set custom ehcache.xml, can not set custom CacheManager,
https://github.com/mybatis/ehcache-cache

This project provide these feature:
* Load Ehcache config from custom file or from a existed ehcache manager.
* Mybatis cache metrics.
* Spring integration.

##Example
mybatis UserMapper xml:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="io.github.xdiamond.persistence.UserMapper">
  <resultMap id="BaseResultMap" type="io.github.xdiamond.domain.User">
  
  <cache type="io.github.hengyunabc.mybatis.FacadeCache">
  </cache>
</mapper>
```
spring application.xml
```xml
    <!-- this bean is important -->
	<bean  class="io.github.hengyunabc.mybatis.SharedSpringContextSetter">
	</bean>
	
	<bean id="ehCacheManager"
		class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean">
		<property name="configLocation" value="classpath:my-ehcache.xml" />
	</bean>
	<!-- this bean name must be mybatisCacheDefaultFactory -->
	<bean id="mybatisCacheDefaultFactory" class="io.github.hengyunabc.mybatis.MybatisEhcacheFactory">
		<property name="cacheManager" ref="ehCacheManager"></property>
	</bean>
	
	<!-- define the SqlSessionFactory -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean" >
		<property name="dataSource" ref="dataSource" />
		<property name="typeAliasesPackage" value="io.github.xdiamond.domain" />
	</bean>

	<!-- scan for mappers and let them be autowired -->
	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer" >
		<property name="basePackage" value="io.github.xdiamond.persistence" />
	</bean>
```
###How it works?
1. SharedSpringContextSetter set the concurrent spring context into `SharedSpringContext`.
1. When mybatis need a cache, it will new a cache object, In UserMapper.xml, cache class is `FacadeCache`.
2. When the `FacadeCache` construct, it will find SpringContext from `SharedSpringContext`, and get the `io.github.hengyunabc.mybatis.CacheFactory` bean. The `CacheFactory` default name is 'mybatisCacheDefaultFactory'.
3. `MybatisEhcacheFactory` will provide the read cache, which wrapped by `FacadeCache`.
```java
public class MybatisEhcacheFactory implements CacheFactory {
  protected CacheManager cacheManager;
  CacheConfiguration cacheConfiguration;

  @Override
  public Cache getCache(String id) {
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
    ```
User can provide custom ehcache `CacheManager` and `CacheConfiguration`.

## Mybatis Cache Metrics
*  Config your metricRegistry.

https://github.com/ryantenney/metrics-spring
```xml
<metrics:metric-registry id="metricRegistry" />
```

* Use `MybatisMetricsEhcacheFactory`.

```xml
	<bean id="mybatisCacheDefaultFactory" class="io.github.hengyunabc.mybatis.MybatisMetricsEhcacheFactory">
		<property name="cacheManager" ref="ehCacheManager"></property>
		<property name="registry" ref="metricRegistry"></property>
	</bean>
```
The metrics result will like these:
```
net.sf.ehcache.Cache.io.github.xdiamond.persistence.UserMapper.eviction-count
             value = 0
net.sf.ehcache.Cache.io.github.xdiamond.persistence.UserMapper.hits
             value = 3
net.sf.ehcache.Cache.io.github.xdiamond.persistence.UserMapper.in-memory-hits
             value = 3
net.sf.ehcache.Cache.io.github.xdiamond.persistence.UserMapper.in-memory-misses
             value = 1
net.sf.ehcache.Cache.io.github.xdiamond.persistence.UserMapper.gets
             count = 4
         mean rate = 0.07 calls/second
     1-minute rate = 0.04 calls/second
     5-minute rate = 0.01 calls/second
    15-minute rate = 0.00 calls/second
               min = 0.06 milliseconds
               max = 0.18 milliseconds
              mean = 0.13 milliseconds
            stddev = 0.05 milliseconds
            median = 0.18 milliseconds
              75% <= 0.18 milliseconds
              95% <= 0.18 milliseconds
              98% <= 0.18 milliseconds
              99% <= 0.18 milliseconds
            99.9% <= 0.18 milliseconds
net.sf.ehcache.Cache.io.github.xdiamond.persistence.UserMapper.puts
             count = 1
         mean rate = 0.02 calls/second
     1-minute rate = 0.01 calls/second
     5-minute rate = 0.00 calls/second
    15-minute rate = 0.00 calls/second
```

## More
Mybatis use mapper namespace for cache id, so you can config different cache for different table in your ehcache.xml

## Licence
Apache License V2