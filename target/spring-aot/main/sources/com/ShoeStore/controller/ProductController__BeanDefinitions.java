package com.ShoeStore.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ProductController}.
 */
@Generated
public class ProductController__BeanDefinitions {
  /**
   * Get the bean definition for 'productController'.
   */
  public static BeanDefinition getProductControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ProductController.class);
    beanDefinition.setInstanceSupplier(ProductController::new);
    return beanDefinition;
  }
}
