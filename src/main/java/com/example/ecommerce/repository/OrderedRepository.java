package com.example.ecommerce.repository;

import com.example.ecommerce.model.Ordered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderedRepository extends JpaRepository<Ordered,Integer> {

    @Query(value = "SELECT * FROM ordered ORDER BY order_date DESC LIMIT 5", nativeQuery = true)
    List<Ordered> recentFiveOrders();

}

//SELECT * FROM product ORDER BY price DESC LIMIT 5