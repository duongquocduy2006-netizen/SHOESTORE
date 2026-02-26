package com.ShoeStore.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link CustomerService}.
 */
@Generated
public class CustomerService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static CustomerService apply(RegisteredBean registeredBean, CustomerService instance) {
    AutowiredFieldValueResolver.forRequiredField("jdbc").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
