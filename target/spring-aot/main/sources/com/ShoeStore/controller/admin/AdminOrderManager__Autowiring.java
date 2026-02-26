package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AdminOrderManager}.
 */
@Generated
public class AdminOrderManager__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AdminOrderManager apply(RegisteredBean registeredBean, AdminOrderManager instance) {
    AutowiredFieldValueResolver.forRequiredField("orderService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
