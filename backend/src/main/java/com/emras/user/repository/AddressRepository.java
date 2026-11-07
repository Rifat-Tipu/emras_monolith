package com.emras.user.repository;
import com.emras.user.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserIdOrderByIsDefaultDesc(Long userId);

    Optional<Address> findByIdAndUserId(Long id, Long userId);

    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);
}