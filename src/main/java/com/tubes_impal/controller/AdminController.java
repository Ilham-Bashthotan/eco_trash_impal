package com.tubes_impal.controller;

import com.tubes_impal.entity.User;
import com.tubes_impal.services.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

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

        // Get dashboard statistics
        Map<String, Object> stats = adminService.getDashboardStats();
        model.addAttribute("totalSellers", stats.get("totalSellers"));
        model.addAttribute("totalCouriers", stats.get("totalCouriers"));
        model.addAttribute("totalOrdersToday", stats.get("totalOrdersToday"));
        model.addAttribute("totalTrashWeight", stats.get("totalTrashWeight"));

        return "admin/dashboard";
    }
}
