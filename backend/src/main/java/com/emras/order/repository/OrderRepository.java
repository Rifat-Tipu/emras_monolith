package com.emras.order.repository;
import com.emras.order.model.Order;
import com.emras.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.items
            WHERE o.id = :id AND o.user.id = :userId
            """)
    Optional<Order> findByIdAndUserId(
            @Param("id") Long id,
            @Param("userId") Long userId);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Admin — all orders
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    @Query("""
            SELECT o FROM Order o
            WHERE o.user.id = :userId
            ORDER BY o.createdAt DESC
            """)
    List<Order> findRecentByUserId(
            @Param("userId") Long userId,
            Pageable pageable);
}