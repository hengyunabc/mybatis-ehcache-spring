package io.github.hengyunabc.mybatis;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.context.ApplicationContext;


public class SharedSpringContext {
  private static final ConcurrentMap<String, ApplicationContext> CONTEXTs =
      new ConcurrentHashMap<String, ApplicationContext>();

  final static public String DEFAULT_SPRINGCONTEXT_NAME = "springContext";

  private SharedSpringContext() { /* singleton */}

  public static void clear() {
    CONTEXTs.clear();
  }

  public static Set<String> names() {
    return CONTEXTs.keySet();
  }

  public static void removeDefaultContext() {
    remove(DEFAULT_SPRINGCONTEXT_NAME);
  }

  public static void remove(String key) {
    CONTEXTs.remove(key);
  }

  public static ApplicationContext addDefaultContext(ApplicationContext context) {
    return add(DEFAULT_SPRINGCONTEXT_NAME, context);
  }

  public static ApplicationContext add(String name, ApplicationContext context) {
    return CONTEXTs.putIfAbsent(name, context);
  }

  public static ApplicationContext getDefaultContext() {
    return get(DEFAULT_SPRINGCONTEXT_NAME);
  }

  public static ApplicationContext get(String name) {
    return CONTEXTs.get(name);
  }
}
