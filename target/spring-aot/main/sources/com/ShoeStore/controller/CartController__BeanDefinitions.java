package com.ShoeStore.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CartController}.
 */
@Generated
public class CartController__BeanDefinitions {
  /**
   * Get the bean definition for 'cartController'.
   */
  public static BeanDefinition getCartControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CartController.class);
    beanDefinition.setInstanceSupplier(CartController::new);
    return beanDefinition;
  }
}
