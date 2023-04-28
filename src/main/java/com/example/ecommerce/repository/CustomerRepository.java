package com.example.ecommerce.repository;

import com.example.ecommerce.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> {

    Customer findByMobNo(String mobNo);

    Customer findByEmailId(String emailId);

    @Query(value = "SELECT * FROM customer WHERE age > 25", nativeQuery = true)
    List<Customer> getAllGreaterThan25();

}
