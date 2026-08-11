package com.emras.cart.repository;

import com.emras.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("""
            SELECT DISTINCT c FROM Cart c
            LEFT JOIN FETCH c.items i
            LEFT JOIN FETCH i.variant v
            LEFT JOIN FETCH v.product p
            WHERE c.user.id = :userId
            """)
    Optional<Cart> findByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT DISTINCT c FROM Cart c
            LEFT JOIN FETCH c.items i
            LEFT JOIN FETCH i.variant v
            LEFT JOIN FETCH v.product p
            WHERE c.sessionId = :sessionId
            """)
    Optional<Cart> findBySessionId(@Param("sessionId") String sessionId);
}