package com.ShoeStore.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link OrderService}.
 */
@Generated
public class OrderService__BeanDefinitions {
  /**
   * Get the bean definition for 'orderService'.
   */
  public static BeanDefinition getOrderServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(OrderService.class);
    InstanceSupplier<OrderService> instanceSupplier = InstanceSupplier.using(OrderService::new);
    instanceSupplier = instanceSupplier.andThen(OrderService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
