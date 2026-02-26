package com.ShoeStore.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link MembershipController}.
 */
@Generated
public class MembershipController__BeanDefinitions {
  /**
   * Get the bean definition for 'membershipController'.
   */
  public static BeanDefinition getMembershipControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MembershipController.class);
    beanDefinition.setInstanceSupplier(MembershipController::new);
    return beanDefinition;
  }
}
