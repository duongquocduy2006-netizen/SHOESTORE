package com.ShoeStore.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link DashboardService}.
 */
@Generated
public class DashboardService__BeanDefinitions {
  /**
   * Get the bean definition for 'dashboardService'.
   */
  public static BeanDefinition getDashboardServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(DashboardService.class);
    InstanceSupplier<DashboardService> instanceSupplier = InstanceSupplier.using(DashboardService::new);
    instanceSupplier = instanceSupplier.andThen(DashboardService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
