package com.tubes_impal.controller;

import com.tubes_impal.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * Admin Dashboard - Requires session authentication
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/admin/login";
        }

        // Check if user is admin
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/auth/admin/login";
        }

        // Add user info to model
        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());

        return "admin/dashboard";
    }
}
