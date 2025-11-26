package com.tubes_impal.controller;

import com.tubes_impal.entity.User;
import com.tubes_impal.services.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Check admin authentication
     */
    private boolean isAdminAuthenticated(HttpSession session) {
        User user = (User) session.getAttribute("user");
        String role = (String) session.getAttribute("role");
        return user != null && "ADMIN".equals(role);
    }

    /**
     * Admin Dashboard - Requires session authentication
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        User user = (User) session.getAttribute("user");

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

    /**
     * Redirect /admin to /admin/dashboard
     */
    @GetMapping("/")
    public String adminRootRedirect() {
        return "redirect:/admin/dashboard";
    }

    /**
     * Courier Management Page
     */
    @GetMapping("/courier-management")
    public String courierManagement(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        User user = (User) session.getAttribute("user");

        // Add user info to model
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());

        // Get courier management data
        Map<String, Object> data = adminService.getCourierManagementData();
        model.addAttribute("calonKurir", data.get("calonKurir"));
        model.addAttribute("kurirAktif", data.get("kurirAktif"));

        return "admin/courier-management";
    }

    /**
     * Hire a courier
     */
    @PostMapping("/courier/hire/{id}")
    public String hireCourier(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        boolean success = adminService.hireCourier(id);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Kurir berhasil di-hire!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Gagal hire kurir. Kurir tidak ditemukan atau sudah aktif.");
        }

        return "redirect:/admin/courier";
    }

    /**
     * Fire a courier
     */
    @PostMapping("/courier/fire/{id}")
    public String fireCourier(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        boolean success = adminService.fireCourier(id);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Kurir berhasil di-fire!");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Gagal fire kurir. Kurir tidak ditemukan atau sudah tidak aktif.");
        }

        return "redirect:/admin/courier";
    }

    /**
     * Trash Statistics Page
     */
    @GetMapping("/trash-statistics")
    public String trashStatistics(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        User user = (User) session.getAttribute("user");

        // Add user info to model
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());

        // Get trash statistics
        Map<String, Object> stats = adminService.getTrashStatistics();
        model.addAttribute("trashOrders", stats.get("trashOrders"));
        model.addAttribute("totalOrders", stats.get("totalOrders"));
        model.addAttribute("totalWeight", stats.get("totalWeight"));
        model.addAttribute("averageWeight", stats.get("averageWeight"));

        return "admin/trash-statistics";
    }

    /**
     * Courier Statistics Page
     */
    @GetMapping("/courier-statistics")
    public String courierStatistics(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        User user = (User) session.getAttribute("user");

        // Add user info to model
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());

        // Get courier statistics
        Map<String, Object> stats = adminService.getCourierStatistics();
        model.addAttribute("courierStats", stats.get("courierStats"));
        model.addAttribute("totalActiveCouriers", stats.get("totalActiveCouriers"));
        model.addAttribute("totalDeliveries", stats.get("totalDeliveries"));
        model.addAttribute("totalOnTime", stats.get("totalOnTime"));
        model.addAttribute("totalLate", stats.get("totalLate"));

        return "admin/courier-statistics";
    }

}
