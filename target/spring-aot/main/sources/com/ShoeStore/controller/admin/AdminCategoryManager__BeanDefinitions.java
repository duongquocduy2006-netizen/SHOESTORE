package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AdminCategoryManager}.
 */
@Generated
public class AdminCategoryManager__BeanDefinitions {
  /**
   * Get the bean definition for 'adminCategoryManager'.
   */
  public static BeanDefinition getAdminCategoryManagerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AdminCategoryManager.class);
    InstanceSupplier<AdminCategoryManager> instanceSupplier = InstanceSupplier.using(AdminCategoryManager::new);
    instanceSupplier = instanceSupplier.andThen(AdminCategoryManager__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
