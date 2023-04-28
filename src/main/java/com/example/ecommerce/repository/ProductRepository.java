package com.example.ecommerce.repository;

import com.example.ecommerce.Enum.ProductCategory;
import com.example.ecommerce.Enum.ProductStatus;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {

    List<Product> findByProductCategory(ProductCategory productCategory);

    List<Product> findByProductStatus(ProductStatus productStatus);

    List<Product> findBySellerEmailId(String emailId);

    @Query(value = "select * from product p where p.price > :price and p.product_category = :category", nativeQuery = true)
    List<Product> getAllProductsByPriceAndCategory(int price, String category);

    @Query(value = "SELECT * FROM product ORDER BY price ASC LIMIT 5", nativeQuery = true)
    List<Product> findFirst5ByOrderByPriceAsc();

    @Query(value = "SELECT * FROM product ORDER BY price DESC LIMIT 5", nativeQuery = true)
    List<Product> findFirst5ByOrderByPriceDesc();
}
