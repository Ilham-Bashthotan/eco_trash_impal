package com.tubes_impal.controller;

import com.tubes_impal.entity.User;
import com.tubes_impal.entity.Contact;
import com.tubes_impal.repos.ContactRepository;
import com.tubes_impal.repos.UserRepository;
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
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    /**
     * Check admin authentication
     */
    private boolean isAdminAuthenticated(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        return userId != null && "ADMIN".equals(role);
    }

    /**
     * Admin Dashboard - Requires session authentication
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/auth/admin/login";
        }
        User user = userOpt.get();

        // Add user info to model
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

    @GetMapping("/profile")
    public String adminProfile(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }
        Integer userId = (Integer) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElse(null);
        Contact contact = contactRepository.findByUserId(userId).orElse(null);
        model.addAttribute("username", user != null ? user.getUsername() : "Admin");
        model.addAttribute("name", user != null ? user.getName() : "Admin");
        model.addAttribute("contact", contact);
        return "admin/admin-profile";
    }

    @GetMapping("/profile/edit")
    public String adminProfileEdit(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }
        Integer userId = (Integer) session.getAttribute("userId");
        Contact contact = contactRepository.findByUserId(userId).orElseGet(() -> {
            Contact c = new Contact();
            User u = userRepository.findById(userId).orElse(null);
            c.setUser(u);
            return c;
        });
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("contact", contact);
        return "admin/admin-profile-edit";
    }

    @PostMapping("/profile/edit")
    public String adminProfileEditSubmit(HttpSession session,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String firstName,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String lastName,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String phoneNumber,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String email,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String address,
            RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }
        Integer userId = (Integer) session.getAttribute("userId");
        Contact contact = contactRepository.findByUserId(userId).orElseGet(() -> {
            Contact c = new Contact();
            User u = userRepository.findById(userId).orElse(null);
            c.setUser(u);
            return c;
        });

        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setPhoneNumber(phoneNumber);
        contact.setEmail(email);
        contact.setAddress(address);
        contactRepository.save(contact);
        redirectAttributes.addFlashAttribute("success", "Profil berhasil diperbarui");
        return "redirect:/admin/profile";
    }

    /**
     * Courier Management Page
     */
    @GetMapping("/courier-management")
    public String courierManagement(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/auth/admin/login";
        }
        User user = userOpt.get();

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

        return "redirect:/admin/courier-management";
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

        return "redirect:/admin/courier-management";
    }

    /**
     * Trash Statistics Page
     */
    @GetMapping("/trash-statistics")
    public String trashStatistics(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/auth/admin/login";
        }
        User user = userOpt.get();

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

        Integer userId = (Integer) session.getAttribute("userId");
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/auth/admin/login";
        }
        User user = userOpt.get();

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

    /**
     * Admin Registration Page
     */
    @GetMapping("/admin-registration")
    public String adminRegistration(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/auth/admin/login";
        }
        User user = userOpt.get();

        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());

        return "admin/admin-registration";
    }

    /**
     * Register New Admin
     */
    @PostMapping("/register-admin")
    public String registerAdmin(
            HttpSession session,
            @org.springframework.web.bind.annotation.RequestParam String username,
            @org.springframework.web.bind.annotation.RequestParam String email,
            @org.springframework.web.bind.annotation.RequestParam String password,
            @org.springframework.web.bind.annotation.RequestParam String phoneNumber,
            RedirectAttributes redirectAttributes) {

        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        try {
            boolean success = adminService.registerAdmin(username, email, password, phoneNumber);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Admin berhasil didaftarkan!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Username atau email sudah digunakan.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mendaftarkan admin: " + e.getMessage());
        }

        return "redirect:/admin/admin-registration";
    }

    /**
     * Courier Registration Page
     */
    @GetMapping("/courier-registration")
    public String courierRegistration(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/auth/admin/login";
        }
        User user = userOpt.get();

        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());

        return "admin/courier-registration";
    }

    /**
     * Register New Courier
     */
    @PostMapping("/register-courier")
    public String registerCourier(
            HttpSession session,
            @org.springframework.web.bind.annotation.RequestParam String username,
            @org.springframework.web.bind.annotation.RequestParam String email,
            @org.springframework.web.bind.annotation.RequestParam String password,
            @org.springframework.web.bind.annotation.RequestParam String phoneNumber,
            @org.springframework.web.bind.annotation.RequestParam String nik,
            @org.springframework.web.bind.annotation.RequestParam String driverLicense,
            RedirectAttributes redirectAttributes) {

        if (!isAdminAuthenticated(session)) {
            return "redirect:/auth/admin/login";
        }

        try {
            boolean success = adminService.registerCourier(username, email, password, phoneNumber, nik, driverLicense);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Kurir berhasil didaftarkan!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Username sudah digunakan.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mendaftarkan kurir: " + e.getMessage());
        }

        return "redirect:/admin/courier-registration";
    }

}
