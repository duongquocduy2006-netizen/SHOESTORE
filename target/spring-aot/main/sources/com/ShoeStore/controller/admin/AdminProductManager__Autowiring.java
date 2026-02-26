package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AdminProductManager}.
 */
@Generated
public class AdminProductManager__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AdminProductManager apply(RegisteredBean registeredBean,
      AdminProductManager instance) {
    AutowiredFieldValueResolver.forRequiredField("productRepository").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("categoryRepository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
