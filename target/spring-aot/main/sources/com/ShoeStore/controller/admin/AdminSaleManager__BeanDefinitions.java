package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AdminSaleManager}.
 */
@Generated
public class AdminSaleManager__BeanDefinitions {
  /**
   * Get the bean definition for 'adminSaleManager'.
   */
  public static BeanDefinition getAdminSaleManagerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AdminSaleManager.class);
    beanDefinition.setInstanceSupplier(AdminSaleManager::new);
    return beanDefinition;
  }
}
