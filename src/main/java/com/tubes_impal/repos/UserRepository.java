package com.tubes_impal.repos;

import com.tubes_impal.entity.User;
import com.tubes_impal.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByToken(String token);
    
    List<User> findByRole(UserRole role);
    
    boolean existsByUsername(String username);
    
    Optional<User> findByUsernameAndPassword(String username, String password);
    
    boolean existsByIdAndRole(Integer id, UserRole role);
}
