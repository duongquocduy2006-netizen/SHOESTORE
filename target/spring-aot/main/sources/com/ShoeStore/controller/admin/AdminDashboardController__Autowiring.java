package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AdminDashboardController}.
 */
@Generated
public class AdminDashboardController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AdminDashboardController apply(RegisteredBean registeredBean,
      AdminDashboardController instance) {
    AutowiredFieldValueResolver.forRequiredField("dashboardService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
