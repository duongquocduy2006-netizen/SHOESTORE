package com.ShoeStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ShoeStore.model.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

}
