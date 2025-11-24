package com.tubes_impal.repos;

import com.tubes_impal.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    
    Optional<Admin> findByUserId(Integer userId);
    
    Optional<Admin> findByUserUsername(String username);
    
    boolean existsByUserId(Integer userId);
}
