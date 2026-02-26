package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AdminProductManager}.
 */
@Generated
public class AdminProductManager__BeanDefinitions {
  /**
   * Get the bean definition for 'adminProductManager'.
   */
  public static BeanDefinition getAdminProductManagerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AdminProductManager.class);
    InstanceSupplier<AdminProductManager> instanceSupplier = InstanceSupplier.using(AdminProductManager::new);
    instanceSupplier = instanceSupplier.andThen(AdminProductManager__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
