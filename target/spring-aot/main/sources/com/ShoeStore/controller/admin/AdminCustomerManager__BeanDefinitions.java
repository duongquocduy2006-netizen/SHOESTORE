package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AdminCustomerManager}.
 */
@Generated
public class AdminCustomerManager__BeanDefinitions {
  /**
   * Get the bean definition for 'adminCustomerManager'.
   */
  public static BeanDefinition getAdminCustomerManagerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AdminCustomerManager.class);
    InstanceSupplier<AdminCustomerManager> instanceSupplier = InstanceSupplier.using(AdminCustomerManager::new);
    instanceSupplier = instanceSupplier.andThen(AdminCustomerManager__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
