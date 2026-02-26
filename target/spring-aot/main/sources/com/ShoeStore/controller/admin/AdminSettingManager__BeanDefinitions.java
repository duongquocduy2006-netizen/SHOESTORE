package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AdminSettingManager}.
 */
@Generated
public class AdminSettingManager__BeanDefinitions {
  /**
   * Get the bean definition for 'adminSettingManager'.
   */
  public static BeanDefinition getAdminSettingManagerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AdminSettingManager.class);
    beanDefinition.setInstanceSupplier(AdminSettingManager::new);
    return beanDefinition;
  }
}
