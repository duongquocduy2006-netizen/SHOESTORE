package com.ShoeStore.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.ShoeStore.model.Product;
import jakarta.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @EntityGraph(attributePaths = {"category", "variants", "images"})
    List<Product> findAll();
    
    // 1. Xóa trong Giỏ hàng (Cấp 1)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cart_items WHERE product_variant_id IN (SELECT id FROM product_variants WHERE product_id = ?1)", nativeQuery = true)
    void deleteRelatedCartItems(Integer productId);

    // 2. Xóa trong Chi tiết đơn hàng (Cấp 1 - Đã sửa đúng tên bảng order_items)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM order_items WHERE product_variant_id IN (SELECT id FROM product_variants WHERE product_id = ?1)", nativeQuery = true)
    void deleteRelatedOrderItems(Integer productId);

    // 3. Xóa Ảnh sản phẩm (Cấp 2)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_images WHERE product_id = ?1", nativeQuery = true)
    void deleteRelatedImages(Integer productId);

    // 4. Xóa Biến thể sản phẩm (Cấp 2 - Cắt đứt liên kết cuối cùng)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_variants WHERE product_id = ?1", nativeQuery = true)
    void deleteRelatedVariants(Integer productId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_reviews WHERE product_id = ?1", nativeQuery = true)
    void deleteRelatedReviews(Integer productId);

 // 5. Xóa Yêu thích (Cấp 1)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM favourites WHERE product_id = ?1", nativeQuery = true)
    void deleteRelatedFavourites(Integer productId);


}