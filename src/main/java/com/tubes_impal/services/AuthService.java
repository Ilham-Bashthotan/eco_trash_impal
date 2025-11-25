package com.tubes_impal.services;

import com.tubes_impal.entity.User;
import com.tubes_impal.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

/**
 * Example Authentication Service
 * Menunjukkan cara verify password yang di-hash oleh seeder
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Verify login credentials
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
        String hashedPassword = hashPassword(password);

        // Verify password
        if (user.getPassword().equals(hashedPassword)) {
            return user;
        }

        return null;
    }

    /**
     * Hash password menggunakan SHA-256
     * Method ini HARUS sama dengan yang digunakan di DataSeeder
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
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
     * Find user by ID
     * 
     * @param userId User ID
     * @return User object
     */
    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }
}
