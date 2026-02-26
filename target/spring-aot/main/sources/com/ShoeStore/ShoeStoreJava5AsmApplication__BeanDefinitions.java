package com.ShoeStore;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ShoeStoreJava5AsmApplication}.
 */
@Generated
public class ShoeStoreJava5AsmApplication__BeanDefinitions {
  /**
   * Get the bean definition for 'shoeStoreJava5AsmApplication'.
   */
  public static BeanDefinition getShoeStoreJavaAsmApplicationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ShoeStoreJava5AsmApplication.class);
    beanDefinition.setInstanceSupplier(ShoeStoreJava5AsmApplication::new);
    return beanDefinition;
  }
}
