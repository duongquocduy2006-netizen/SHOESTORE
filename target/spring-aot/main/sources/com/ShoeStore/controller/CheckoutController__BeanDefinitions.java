package com.ShoeStore.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CheckoutController}.
 */
@Generated
public class CheckoutController__BeanDefinitions {
  /**
   * Get the bean definition for 'checkoutController'.
   */
  public static BeanDefinition getCheckoutControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CheckoutController.class);
    beanDefinition.setInstanceSupplier(CheckoutController::new);
    return beanDefinition;
  }
}
