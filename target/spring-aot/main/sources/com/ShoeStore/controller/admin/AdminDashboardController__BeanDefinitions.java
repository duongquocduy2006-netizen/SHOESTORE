package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AdminDashboardController}.
 */
@Generated
public class AdminDashboardController__BeanDefinitions {
  /**
   * Get the bean definition for 'adminDashboardController'.
   */
  public static BeanDefinition getAdminDashboardControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AdminDashboardController.class);
    InstanceSupplier<AdminDashboardController> instanceSupplier = InstanceSupplier.using(AdminDashboardController::new);
    instanceSupplier = instanceSupplier.andThen(AdminDashboardController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
