package com.tubes_impal.repos;

import com.tubes_impal.entity.Trash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrashRepository extends JpaRepository<Trash, Integer> {
    
    List<Trash> findByAddressContaining(String address);
    
    List<Trash> findByTrashWeightGreaterThanEqual(Double minWeight);
    
    List<Trash> findByTrashWeightBetween(Double minWeight, Double maxWeight);
    
    Long countByAddressContaining(String address);
}
