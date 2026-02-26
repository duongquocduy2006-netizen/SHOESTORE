package com.ShoeStore.controller.admin;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AdminCategoryManager}.
 */
@Generated
public class AdminCategoryManager__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AdminCategoryManager apply(RegisteredBean registeredBean,
      AdminCategoryManager instance) {
    AutowiredFieldValueResolver.forRequiredField("categoryRepo").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
