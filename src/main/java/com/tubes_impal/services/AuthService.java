package com.tubes_impal.services;

import com.tubes_impal.entity.Contact;
import com.tubes_impal.entity.User;
import com.tubes_impal.entity.Seller;
import com.tubes_impal.repos.ContactRepository;
import com.tubes_impal.repos.UserRepository;
import com.tubes_impal.repos.SellerRepository;
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
    private SellerRepository sellerRepository;

    @Autowired
    private ContactRepository contactRepository;

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
     * Register new seller
     * 
     * @param username Username untuk login
     * @param email    Email seller (akan disimpan di Contact)
     * @param password Plain text password
     * @return User object yang sudah disimpan
     * @throws Exception Jika username sudah terdaftar
     */
    public User registerSeller(String username, String email, String password) throws Exception {

        Optional<User> existingUserByUsername = userRepository.findByUsername(username);
        if (existingUserByUsername.isPresent()) {
            throw new Exception("Username sudah terdaftar");
        }

        User newSeller = new User();
        newSeller.setUsername(username);
        newSeller.setName(username); // Set name sama dengan username
        newSeller.setPassword(password); // Akan di-hash oleh register()
        newSeller.setRole(com.tubes_impal.entity.UserRole.SELLER);

        User savedUser = register(newSeller);

        // Create Contact dan simpan email
        Contact contact = new Contact();
        contact.setUser(savedUser);
        contact.setEmail(email);
        contactRepository.save(contact);

        Seller seller = new Seller();
        seller.setUser(savedUser);
        seller.setBalance(0.0);
        sellerRepository.save(seller);

        return savedUser;
    }
}
