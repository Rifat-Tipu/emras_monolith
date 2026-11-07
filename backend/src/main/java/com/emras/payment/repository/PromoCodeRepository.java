package com.emras.payment.repository;
import com.emras.payment.model.PromoCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    // Normal read — no lock (for display/validation preview only)
    Optional<PromoCode> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    // Locked read — used during actual order placement
    // PESSIMISTIC_WRITE locks the row until the transaction commits
    // So if User A is placing an order, User B's request waits until A finishes
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PromoCode p WHERE LOWER(p.code) = LOWER(:code)")
    Optional<PromoCode> findByCodeForUpdate(@Param("code") String code);
}