package io.github.hengyunabc.mybatis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * add current spring context into global SharedSpringContext
 * 
 * @author hengyunabc
 *
 */
public class SharedSpringContextSetter implements ApplicationContextAware, BeanFactoryPostProcessor {

  String contextName = null;;

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    if (contextName == null) {
      SharedSpringContext.addDefaultContext(context);
    } else {
      SharedSpringContext.add(contextName, context);
    }
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory arg0) throws BeansException {

  }

  public String getContextName() {
    return contextName;
  }

  public void setContextName(String contextName) {
    this.contextName = contextName;
  }
}
