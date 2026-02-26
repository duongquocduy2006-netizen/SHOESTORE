package com.ShoeStore.config;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.ConfigurationClassUtils;
import org.springframework.core.ResolvableType;

/**
 * Bean definitions for {@link TomcatConfig}.
 */
@Generated
public class TomcatConfig__BeanDefinitions {
  /**
   * Get the bean definition for 'tomcatConfig'.
   */
  public static BeanDefinition getTomcatConfigBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TomcatConfig.class);
    beanDefinition.setTargetType(TomcatConfig.class);
    ConfigurationClassUtils.initializeConfigurationClass(TomcatConfig.class);
    beanDefinition.setInstanceSupplier(TomcatConfig$$SpringCGLIB$$0::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'tomcatCustomizer'.
   */
  private static BeanInstanceSupplier<WebServerFactoryCustomizer> getTomcatCustomizerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<WebServerFactoryCustomizer>forFactoryMethod(TomcatConfig$$SpringCGLIB$$0.class, "tomcatCustomizer")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean("tomcatConfig", TomcatConfig.class).tomcatCustomizer());
  }

  /**
   * Get the bean definition for 'tomcatCustomizer'.
   */
  public static BeanDefinition getTomcatCustomizerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(WebServerFactoryCustomizer.class);
    beanDefinition.setTargetType(ResolvableType.forClassWithGenerics(WebServerFactoryCustomizer.class, TomcatServletWebServerFactory.class));
    beanDefinition.setFactoryBeanName("tomcatConfig");
    beanDefinition.setInstanceSupplier(getTomcatCustomizerInstanceSupplier());
    return beanDefinition;
  }
}
