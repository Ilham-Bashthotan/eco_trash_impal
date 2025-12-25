package com.tubes_impal.controller;

import com.tubes_impal.entity.User;
import com.tubes_impal.services.AuthService;
import com.tubes_impal.utils.MultiSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    // ====================== ADMIN ======================

    @GetMapping("/admin/login")
    public String showAdminLoginPage(HttpServletRequest request, Model model) {
        if (MultiSessionManager.isAuthenticated(request, "ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        return "auth/admin-login";
    }

    @PostMapping("/admin/login")
    public String adminLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Username dan password tidak boleh kosong");
            return "redirect:/auth/admin/login";
        }

        User user = authService.login(username, password);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Username atau password salah");
            return "redirect:/auth/admin/login";
        }

        if (!"ADMIN".equals(user.getRole().toString())) {
            redirectAttributes.addFlashAttribute("error", "Akses ditolak. Hanya admin yang diizinkan.");
            return "redirect:/auth/admin/login";
        }

        MultiSessionManager.setSessionAttribute(request, response, "ADMIN", "userId", user.getId());
        MultiSessionManager.setSessionAttribute(request, response, "ADMIN", "username", user.getUsername());
        MultiSessionManager.setSessionAttribute(request, response, "ADMIN", "role", user.getRole().toString());

        redirectAttributes.addFlashAttribute("success", "Login berhasil!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/logout")
    public String adminLogout(HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        MultiSessionManager.invalidateSession(request, response, "ADMIN");
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/admin/login";
    }

    @GetMapping("/admin/logout")
    public String adminLogoutGet(HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        MultiSessionManager.invalidateSession(request, response, "ADMIN");
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/admin/login";
    }

    // ====================== COURIER ======================

    @GetMapping("/courier/login")
    public String showCourierLoginPage(HttpServletRequest request, Model model) {
        if (MultiSessionManager.isAuthenticated(request, "COURIER")) {
            return "redirect:/courier/dashboard";
        }
        return "auth/courier-login";
    }

    @PostMapping("/courier/login")
    public String courierLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Username dan password tidak boleh kosong");
            return "redirect:/auth/courier/login";
        }

        User user = authService.login(username, password);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Username atau password salah");
            return "redirect:/auth/courier/login";
        }

        if (!"COURIER".equals(user.getRole().toString())) {
            redirectAttributes.addFlashAttribute("error", "Akses ditolak. Hanya courier yang diizinkan.");
            return "redirect:/auth/courier/login";
        }

        MultiSessionManager.setSessionAttribute(request, response, "COURIER", "userId", user.getId());
        MultiSessionManager.setSessionAttribute(request, response, "COURIER", "username", user.getUsername());
        MultiSessionManager.setSessionAttribute(request, response, "COURIER", "role", user.getRole().toString());

        redirectAttributes.addFlashAttribute("success", "Login courier berhasil!");
        return "redirect:/courier/dashboard";
    }

    @PostMapping("/courier/logout")
    public String courierLogout(HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        MultiSessionManager.invalidateSession(request, response, "COURIER");
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/courier/login";
    }

    @GetMapping("/courier/logout")
    public String courierLogoutGet(HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        MultiSessionManager.invalidateSession(request, response, "COURIER");
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/courier/login";
    }

    // =================== SELLER ===================

    @GetMapping("/seller/login")
    public String showSellerLoginPage(HttpServletRequest request) {
        if (MultiSessionManager.isAuthenticated(request, "SELLER")) {
            return "redirect:/seller/dashboard";
        }
        return "auth/seller-login";
    }

    @PostMapping("/seller/login")
    public String sellerLogin(@RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Username dan password tidak boleh kosong");
            return "redirect:/auth/seller/login";
        }

        User user = authService.login(username, password);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Username atau password salah");
            return "redirect:/auth/seller/login";
        }

        // role seller
        if (!"SELLER".equals(user.getRole().toString())) {
            redirectAttributes.addFlashAttribute("error", "Akses ditolak. Hanya seller yang diizinkan.");
            return "redirect:/auth/seller/login";
        }

        MultiSessionManager.setSessionAttribute(request, response, "SELLER", "userId", user.getId());
        MultiSessionManager.setSessionAttribute(request, response, "SELLER", "username", user.getUsername());
        MultiSessionManager.setSessionAttribute(request, response, "SELLER", "role", user.getRole().toString());

        redirectAttributes.addFlashAttribute("success", "Login berhasil!");
        return "redirect:/seller/dashboard";
    }

    // ====================== SELLER SIGN IN======================

    @GetMapping("/seller/signin")
    public String showSellerSignInPage(HttpServletRequest request) {
        if (MultiSessionManager.isAuthenticated(request, "SELLER")) {
            return "redirect:/seller/dashboard";
        }
        return "auth/seller-signin";
    }

    @PostMapping("/seller/register")
    public String sellerRegister(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Semua field harus diisi");
            return "redirect:/auth/seller/signin";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Password dan konfirmasi password tidak cocok");
            return "redirect:/auth/seller/signin";
        }

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password minimal 6 karakter");
            return "redirect:/auth/seller/signin";
        }

        try {
            authService.registerSeller(username, email, password);
            redirectAttributes.addFlashAttribute("success", "Registrasi berhasil! Silakan login");
            return "redirect:/auth/seller/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registrasi gagal: " + e.getMessage());
            return "redirect:/auth/seller/signin";
        }
    }

    @PostMapping("/seller/logout")
    public String sellerLogout(HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        MultiSessionManager.invalidateSession(request, response, "SELLER");
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/seller/login";
    }

    @GetMapping("/seller/logout")
    public String sellerLogoutGet(HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        MultiSessionManager.invalidateSession(request, response, "SELLER");
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/seller/login";
    }

}
