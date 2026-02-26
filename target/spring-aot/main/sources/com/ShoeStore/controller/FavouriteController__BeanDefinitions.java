package com.ShoeStore.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link FavouriteController}.
 */
@Generated
public class FavouriteController__BeanDefinitions {
  /**
   * Get the bean definition for 'favouriteController'.
   */
  public static BeanDefinition getFavouriteControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(FavouriteController.class);
    beanDefinition.setInstanceSupplier(FavouriteController::new);
    return beanDefinition;
  }
}
