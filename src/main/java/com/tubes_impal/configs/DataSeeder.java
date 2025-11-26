package com.tubes_impal.configs;

import com.tubes_impal.entity.Admin;
import com.tubes_impal.entity.User;
import com.tubes_impal.entity.UserRole;
import com.tubes_impal.repos.AdminRepository;
import com.tubes_impal.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
            AdminRepository adminRepository) {
        return args -> {
            // Cek apakah super admin sudah ada
            if (userRepository.findByUsername("superadmin").isEmpty()) {
                // Buat user super admin
                User superAdminUser = new User();
                superAdminUser.setName("Super Admin");
                superAdminUser.setUsername("superadmin");
                // Hash password menggunakan BCrypt
                superAdminUser.setPassword(passwordEncoder.encode("admin123"));
                superAdminUser.setRole(UserRole.ADMIN);

                // Save user terlebih dahulu
                User savedUser = userRepository.save(superAdminUser);

                // Buat admin entity
                Admin admin = new Admin();
                admin.setUser(savedUser);

                // Save admin
                adminRepository.save(admin);

                System.out.println("========================================");
                System.out.println("✓ Super Admin berhasil dibuat!");
                System.out.println("  Username: superadmin");
                System.out.println("  Password: admin123");
                System.out.println("========================================");
            } else {
                System.out.println("✓ Super Admin sudah ada di database");
            }
        };
    }
}
