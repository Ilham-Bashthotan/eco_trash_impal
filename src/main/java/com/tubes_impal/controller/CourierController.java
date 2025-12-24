package com.tubes_impal.controller;

import com.tubes_impal.entity.Contact;
import com.tubes_impal.entity.User;
import com.tubes_impal.repos.ContactRepository;
import com.tubes_impal.repos.UserRepository;
import com.tubes_impal.services.CourierService;
import com.tubes_impal.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/courier")
public class CourierController {

    @Autowired
    private CourierService courierService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    private boolean isCourierAuthenticated(HttpServletRequest request) {
        return SessionHelper.isAuthenticated(request, "COURIER");
    }

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/courier/dashboard";
    }

    @GetMapping("/profile")
    public String courierProfile(HttpServletRequest request, Model model) {
        if (!isCourierAuthenticated(request)) {
            return "redirect:/auth/courier/login";
        }
        Integer userId = SessionHelper.getUserId(request, "COURIER");
        User user = userRepository.findById(userId).orElse(null);
        Contact contact = contactRepository.findByUserId(userId).orElse(null);
        model.addAttribute("courierName", user != null ? user.getName() : "Courier");
        model.addAttribute("contact", contact);
        return "courier/courier-profile";
    }

    @GetMapping("/profile/edit")
    public String courierProfileEdit(HttpServletRequest request, Model model) {
        if (!isCourierAuthenticated(request)) {
            return "redirect:/auth/courier/login";
        }
        Integer userId = SessionHelper.getUserId(request, "COURIER");
        Contact contact = contactRepository.findByUserId(userId).orElseGet(() -> {
            Contact c = new Contact();
            User u = userRepository.findById(userId).orElse(null);
            c.setUser(u);
            return c;
        });
        model.addAttribute("courierName", SessionHelper.getUsername(request, "COURIER"));
        model.addAttribute("contact", contact);
        return "courier/courier-profile-edit";
    }

    @PostMapping("/profile/edit")
    public String courierProfileEditSubmit(HttpServletRequest request,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String firstName,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String lastName,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String phoneNumber,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String email,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String address,
            RedirectAttributes redirectAttributes) {
        if (!isCourierAuthenticated(request)) {
            return "redirect:/auth/courier/login";
        }
        Integer userId = SessionHelper.getUserId(request, "COURIER");
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
        return "redirect:/courier/profile";
    }

    @GetMapping("/dashboard")
    public String CourierDashboard(HttpServletRequest request, Model model) {
        if (!isCourierAuthenticated(request)) {
            return "redirect:/auth/courier/login";
        }
        Integer userId = SessionHelper.getUserId(request, "COURIER");

        try {
            Map<String, Object> dashboardData = courierService.getDashboardData(userId);
            model.addAllAttributes(dashboardData);
        } catch (Exception e) {
            model.addAttribute("errorLoading", true);
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "courier/courier-dashboard";
    }

    @GetMapping("/orders")
    public String CourierOrders(HttpServletRequest request, Model model) {
        if (!isCourierAuthenticated(request)) {
            return "redirect:/auth/courier/login";
        }
        Integer userId = SessionHelper.getUserId(request, "COURIER");

        try {
            List<Map<String, Object>> orders = courierService.getOrders(userId);
            model.addAttribute("courierName", ((User) model.asMap().get("courierName")));
            model.addAttribute("orders", orders);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "courier/courier-orders";
    }

    @GetMapping("/orders/{id}")
    public String CourierOrderDetail(HttpServletRequest request, @PathVariable Long id, Model model) {
        if (!isCourierAuthenticated(request)) {
            return "redirect:/auth/courier/login";
        }
        Integer userId = SessionHelper.getUserId(request, "COURIER");

        try {
            Map<String, Object> order = courierService.getOrderDetail(userId, id);
            model.addAttribute("order", order);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "courier/courier-order-detail";
    }
}
