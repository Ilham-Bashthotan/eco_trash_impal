package com.tubes_impal.repos;

import com.tubes_impal.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {

    Optional<Seller> findByUserId(Integer userId);

    Optional<Seller> findByUserUsername(String username);

    boolean existsByUserId(Integer userId);

    List<Seller> findByBalanceGreaterThanEqual(Double minBalance);
}
