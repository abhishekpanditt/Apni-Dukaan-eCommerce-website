package com.example.ecommerce.repository;

import com.example.ecommerce.Enum.CardType;
import com.example.ecommerce.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card,Integer> {

    List<Card> findByCardType(CardType cardType);
    Card findByCardNo(String cardNo);

    @Query(value = "SELECT * FROM card c WHERE c.expiry_date > :date", nativeQuery = true)
    List<Card> findAllMastercardGreaterThan(Date date);
}
