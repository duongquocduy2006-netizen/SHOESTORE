package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AdminPromotionsManager}.
 */
@Generated
public class AdminPromotionsManager__BeanDefinitions {
  /**
   * Get the bean definition for 'adminPromotionsManager'.
   */
  public static BeanDefinition getAdminPromotionsManagerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AdminPromotionsManager.class);
    beanDefinition.setInstanceSupplier(AdminPromotionsManager::new);
    return beanDefinition;
  }
}
