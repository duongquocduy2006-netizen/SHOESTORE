package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AdminCustomerManager}.
 */
@Generated
public class AdminCustomerManager__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AdminCustomerManager apply(RegisteredBean registeredBean,
      AdminCustomerManager instance) {
    AutowiredFieldValueResolver.forRequiredField("customerService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
