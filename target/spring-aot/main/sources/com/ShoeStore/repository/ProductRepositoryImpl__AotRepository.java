package com.ShoeStore.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.Integer;
import java.lang.String;
import org.springframework.aot.generate.Generated;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;

/**
 * AOT generated JPA repository implementation for {@link ProductRepository}.
 */
@Generated
public class ProductRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public ProductRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link ProductRepository#deleteRelatedCartItems(java.lang.Integer)}.
   */
  public void deleteRelatedCartItems(Integer productId) {
    String queryString = "DELETE FROM cart_items WHERE product_variant_id IN (SELECT id FROM product_variants WHERE product_id = ?1)";
    Query query = this.entityManager.createNativeQuery(queryString);
    query.setParameter(1, productId);

    query.executeUpdate();
  }

  /**
   * AOT generated implementation of {@link ProductRepository#deleteRelatedImages(java.lang.Integer)}.
   */
  public void deleteRelatedImages(Integer productId) {
    String queryString = "DELETE FROM product_images WHERE product_id = ?1";
    Query query = this.entityManager.createNativeQuery(queryString);
    query.setParameter(1, productId);

    query.executeUpdate();
  }

  /**
   * AOT generated implementation of {@link ProductRepository#deleteRelatedVariants(java.lang.Integer)}.
   */
  public void deleteRelatedVariants(Integer productId) {
    String queryString = "DELETE FROM product_variants WHERE product_id = ?1";
    Query query = this.entityManager.createNativeQuery(queryString);
    query.setParameter(1, productId);

    query.executeUpdate();
  }
}
