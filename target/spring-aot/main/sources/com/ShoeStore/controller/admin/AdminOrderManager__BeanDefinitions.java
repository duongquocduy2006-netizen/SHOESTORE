package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AdminOrderManager}.
 */
@Generated
public class AdminOrderManager__BeanDefinitions {
  /**
   * Get the bean definition for 'adminOrderManager'.
   */
  public static BeanDefinition getAdminOrderManagerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AdminOrderManager.class);
    InstanceSupplier<AdminOrderManager> instanceSupplier = InstanceSupplier.using(AdminOrderManager::new);
    instanceSupplier = instanceSupplier.andThen(AdminOrderManager__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
