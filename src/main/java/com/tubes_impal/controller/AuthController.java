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

    // ====================== ADMIN ======================

    @GetMapping("/admin/login")
    public String showAdminLoginPage(HttpSession session, Model model) {
        if (session.getAttribute("userId") != null &&
                "ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/admin/dashboard";
        }
        return "auth/admin-login";
    }

    @PostMapping("/admin/login")
    public String adminLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
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

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().toString());

        redirectAttributes.addFlashAttribute("success", "Login berhasil!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/logout")
    public String adminLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/admin/login";
    }

    @GetMapping("/admin/logout")
    public String adminLogoutGet(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/admin/login";
    }

    // ====================== COURIER ======================

    @GetMapping("/courier/login")
    public String showCourierLoginPage(HttpSession session, Model model) {
        if (session.getAttribute("userId") != null &&
                "COURIER".equals(session.getAttribute("role"))) {
            return "redirect:/courier/dashboard";
        }
        return "auth/courier-login";
    }

    @PostMapping("/courier/login")
    public String courierLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
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

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().toString());

        redirectAttributes.addFlashAttribute("success", "Login courier berhasil!");
        return "redirect:/courier/dashboard";
    }

    @PostMapping("/courier/logout")
    public String courierLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/courier/login";
    }

    @GetMapping("/courier/logout")
    public String courierLogoutGet(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/courier/login";
    }

// =================== SELLER ===================

    @GetMapping("/seller/login")
    public String showSellerLoginPage(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/seller/dashboard";
        }
        return "auth/seller-login";
    }

    @PostMapping("/seller/login")
    public String sellerLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
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

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().toString());

        redirectAttributes.addFlashAttribute("success", "Login berhasil!");
        return "redirect:/seller/dashboard";
    }

        // ====================== SELLER SIGN IN======================

    @GetMapping("/seller/signin")
    public String showSellerSignInPage(HttpSession session) {
        if (session.getAttribute("userId") != null) {
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
            User newUser = authService.registerSeller(username, email, password);
            redirectAttributes.addFlashAttribute("success", "Registrasi berhasil! Silakan login");
            return "redirect:/auth/seller/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registrasi gagal: " + e.getMessage());
            return "redirect:/auth/seller/signin";
        }
    }

    @PostMapping("/seller/logout")
    public String sellerLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/seller/login";
    }

    @GetMapping("/seller/logout")
    public String sellerLogoutGet(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logout berhasil!");
        return "redirect:/auth/seller/login";
    }

}
