package com.tubes_impal.repos;

import com.tubes_impal.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    
    Optional<Contact> findByUserId(Integer userId);
    
    Optional<Contact> findByEmail(String email);
    
    Optional<Contact> findByPhoneNumber(String phoneNumber);
}
