package com.tubes_impal.services;

import com.tubes_impal.entity.User;
import com.tubes_impal.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Authentication Service with BCrypt password hashing
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Verify login credentials
     * Note: Spring Security akan handle authentication secara otomatis,
     * method ini hanya untuk backward compatibility atau manual login
     * 
     * @param username Username
     * @param password Plain text password
     * @return User object jika berhasil, null jika gagal
     */
    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();

        // Verify password menggunakan BCrypt
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        return null;
    }

    /**
     * Hash password menggunakan BCrypt
     */
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Register new user with hashed password
     * 
     * @param user User object with plain text password
     * @return Saved user with hashed password
     */
    public User register(User user) {
        // Hash password sebelum save
        user.setPassword(hashPassword(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * @param userId 
     * @return
     */
    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    /**
     * @param username 
     * @param email 
     * @param password 
     * @return 
     * @throws Exception 
     */
    public User registerSeller(String username, String email, String password) throws Exception {

        Optional<User> existingUserByUsername = userRepository.findByUsername(username);
        if (existingUserByUsername.isPresent()) {
            throw new Exception("Username sudah terdaftar");
        }

        User newSeller = new User();
        newSeller.setUsername(username);
        newSeller.setEmail(email);
        newSeller.setPassword(password);
        newSeller.setRole(com.tubes_impal.entity.UserRole.SELLER);

        return register(newSeller);
    }
}
