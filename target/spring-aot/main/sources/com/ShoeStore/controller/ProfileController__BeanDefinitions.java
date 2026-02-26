package com.ShoeStore.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ProfileController}.
 */
@Generated
public class ProfileController__BeanDefinitions {
  /**
   * Get the bean definition for 'profileController'.
   */
  public static BeanDefinition getProfileControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ProfileController.class);
    beanDefinition.setInstanceSupplier(ProfileController::new);
    return beanDefinition;
  }
}
