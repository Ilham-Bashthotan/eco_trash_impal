package com.tubes_impal.controller;

import com.tubes_impal.entity.User;
import com.tubes_impal.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Halaman login admin (View)
     */
    @GetMapping("/admin/login")
    public String showAdminLoginPage(HttpSession session, Model model) {
        // Jika sudah login, redirect ke dashboard
        if (session.getAttribute("userId") != null) {
            return "redirect:/admin/dashboard";
        }
        return "auth/admin-login";
    }

    /**
     * Process login admin (MVC form submission)
     */
    @PostMapping("/admin/login")
    public String adminLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Validasi input
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Username dan password tidak boleh kosong");
            return "redirect:/auth/admin/login";
        }

        // Autentikasi user
        User user = authService.login(username, password);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Username atau password salah");
            return "redirect:/auth/admin/login";
        }

        // Check role - hanya admin yang boleh login
        if (!"ADMIN".equals(user.getRole().toString())) {
            redirectAttributes.addFlashAttribute("error", "Akses ditolak. Hanya admin yang diizinkan.");
            return "redirect:/auth/admin/login";
        }

        // Simpan data user di session (tidak perlu object User utuh)
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().toString());

        redirectAttributes.addFlashAttribute("success", "Login berhasil!");
        return "redirect:/admin/dashboard";
    }

    /**
     * Logout admin
     */
    @PostMapping("/admin/logout")
    public String adminLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/admin/login";
    }

    /**
     * Logout via GET (untuk link logout)
     */
    @GetMapping("/admin/logout")
    public String adminLogoutGet(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/admin/login";
    }
    
}
