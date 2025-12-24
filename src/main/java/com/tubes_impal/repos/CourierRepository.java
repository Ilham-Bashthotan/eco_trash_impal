package com.tubes_impal.repos;

import com.tubes_impal.entity.Courier;
import com.tubes_impal.entity.StatusCourier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Integer> {

    Optional<Courier> findByUserId(Integer userId);

    Optional<Courier> findByUserUsername(String username);

    boolean existsByUserId(Integer userId);

    List<Courier> findByStatus(StatusCourier status);

    List<Courier> findByHiredById(Integer adminId);

    List<Courier> findAllByOrderBySuccessfulDeliveriesDesc();
}
