package com.tubes_impal.repos;

import com.tubes_impal.entity.StatusOrder;
import com.tubes_impal.entity.TrashOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrashOrderRepository extends JpaRepository<TrashOrder, Integer> {

    List<TrashOrder> findByStatus(StatusOrder status);

    List<TrashOrder> findBySellerId(Integer sellerId);

    List<TrashOrder> findByCourierId(Integer courierId);

    List<TrashOrder> findBySellerUserId(Integer userId);

    List<TrashOrder> findByCourierUserId(Integer userId);

    List<TrashOrder> findBySellerIdAndStatus(Integer sellerId, StatusOrder status);

    List<TrashOrder> findByCourierIdAndStatus(Integer courierId, StatusOrder status);

    List<TrashOrder> findByStatusAndCourierIsNull(StatusOrder status);

    List<TrashOrder> findByTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    Long countByStatus(StatusOrder status);

    Long countBySellerId(Integer sellerId);

    Long countByCourierId(Integer courierId);

    List<TrashOrder> findAllByOrderByTimeDesc();
}
