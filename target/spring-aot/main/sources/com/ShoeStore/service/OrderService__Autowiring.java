package com.ShoeStore.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link OrderService}.
 */
@Generated
public class OrderService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static OrderService apply(RegisteredBean registeredBean, OrderService instance) {
    AutowiredFieldValueResolver.forRequiredField("jdbc").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
