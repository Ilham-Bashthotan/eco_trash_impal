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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tubes_impal.entity.StatusOrder;
import com.tubes_impal.entity.TrashOrder;
import com.tubes_impal.entity.Courier;
import com.tubes_impal.entity.Seller;
import com.tubes_impal.repos.SellerRepository;
import com.tubes_impal.repos.TrashOrderRepository;
import com.tubes_impal.repos.CourierRepository;
import java.util.*;
import java.util.Optional;

@Controller
@RequestMapping("/courier")
public class CourierController {

    @Autowired
    private CourierService courierService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private TrashOrderRepository trashOrderRepository;

    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private SellerRepository sellerRepository;

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
        Courier courier = courierRepository.findByUserId(userId).orElse(null);
        model.addAttribute("courierName", user != null ? user.getName() : "Courier");
        model.addAttribute("courierStatus", courier != null ? courier.getStatus() : null);
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
        Courier courier = courierRepository.findByUserId(userId).orElse(null);
        model.addAttribute("courierName", SessionHelper.getUsername(request, "COURIER"));
        model.addAttribute("courierStatus", courier != null ? courier.getStatus() : null);
        model.addAttribute("contact", contact);
        return "courier/courier-profile-edit";
    }

    @PostMapping("/profile/edit")
    public String courierProfileEditSubmit(HttpServletRequest request,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String firstName,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String lastName,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String phoneNumber,
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
            User user = userRepository.findById(userId).orElse(null);
            Courier courier = courierRepository.findByUserId(userId).orElse(null);
            model.addAttribute("courierName", user != null ? user.getName() : "Courier");
            model.addAttribute("courierStatus", courier != null ? courier.getStatus() : null);
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
            User user = userRepository.findById(userId).orElse(null);
            Courier courier = courierRepository.findByUserId(userId).orElse(null);
            model.addAttribute("courierName", user != null ? user.getName() : "Courier");
            model.addAttribute("courierStatus", courier != null ? courier.getStatus() : null);
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
            User user = userRepository.findById(userId).orElse(null);
            Courier courier = courierRepository.findByUserId(userId).orElse(null);
            model.addAttribute("courierName", user != null ? user.getName() : "Courier");
            model.addAttribute("courierStatus", courier != null ? courier.getStatus() : null);
            model.addAttribute("order", order);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "courier/courier-order-detail";
    }

    @PostMapping("/orders/{id}/pickup")
    public String pickupOrder(
            HttpServletRequest request,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        if (!isCourierAuthenticated(request)) {
            return "redirect:/auth/courier/login";
        }

        Integer userId = SessionHelper.getUserId(request, "COURIER");

        try {
            Optional<TrashOrder> orderOpt = trashOrderRepository.findById(id.intValue());
            if (orderOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Order tidak ditemukan");
                return "redirect:/courier/orders";
            }

            TrashOrder order = orderOpt.get();
            if (order.getCourier() == null || !order.getCourier().getUser().getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "Order tidak dimiliki courier ini");
                return "redirect:/courier/orders";
            }

            if (order.getStatus() != StatusOrder.PENDING) {
                redirectAttributes.addFlashAttribute("error", "Status order tidak valid untuk diambil");
                return "redirect:/courier/orders/" + id;
            }

            order.setStatus(StatusOrder.PICKED_UP);
            trashOrderRepository.save(order);
            redirectAttributes.addFlashAttribute("success", "Pesanan berhasil diambil. Silakan isi berat sampah.");
            return "redirect:/courier/orders/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/courier/orders";
        }
    }

    @PostMapping("/orders/{id}/deliver")
    public String deliverOrder(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam(required = false) Double weight,
            RedirectAttributes redirectAttributes) {
        if (!isCourierAuthenticated(request)) {
            return "redirect:/auth/courier/login";
        }

        Integer userId = SessionHelper.getUserId(request, "COURIER");

        try {
            if (weight == null || weight <= 0) {
                redirectAttributes.addFlashAttribute("error", "Berat sampah harus diisi (lebih dari 0)");
                return "redirect:/courier/orders/" + id;
            }

            if (weight < 0.1 || weight > 100) {
                redirectAttributes.addFlashAttribute("error", "Berat sampah harus antara 0.1 kg hingga 100 kg");
                return "redirect:/courier/orders/" + id;
            }

            Optional<TrashOrder> orderOpt = trashOrderRepository.findById(id.intValue());
            if (orderOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Order tidak ditemukan");
                return "redirect:/courier/orders";
            }

            TrashOrder order = orderOpt.get();
            if (order.getCourier() == null || !order.getCourier().getUser().getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "Order tidak dimiliki courier ini");
                return "redirect:/courier/orders";
            }

            if (order.getStatus() != StatusOrder.PICKED_UP) {
                redirectAttributes.addFlashAttribute("error", "Status order harus PICKED_UP untuk dikirim");
                return "redirect:/courier/orders/" + id;
            }

            if (order.getTrash() != null) {
                order.getTrash().setTrashWeight(weight);
            }
            order.setStatus(StatusOrder.DELIVERED);
            trashOrderRepository.save(order);
            redirectAttributes.addFlashAttribute("success", "Pesanan berhasil dikirim. Berat: " + weight + "kg");
            return "redirect:/courier/orders/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/courier/orders/" + id;
        }
    }

    @PostMapping("/orders/{id}/complete")
    public String completeOrder(
            HttpServletRequest request,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        if (!isCourierAuthenticated(request)) {
            return "redirect:/auth/courier/login";
        }

        Integer userId = SessionHelper.getUserId(request, "COURIER");

        try {
            Optional<TrashOrder> orderOpt = trashOrderRepository.findById(id.intValue());
            if (orderOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Order tidak ditemukan");
                return "redirect:/courier/orders";
            }

            TrashOrder order = orderOpt.get();
            if (order.getCourier() == null || !order.getCourier().getUser().getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "Order tidak dimiliki courier ini");
                return "redirect:/courier/orders";
            }

            if (order.getStatus() != StatusOrder.DELIVERED) {
                redirectAttributes.addFlashAttribute("error", "Status order harus DELIVERED untuk diselesaikan");
                return "redirect:/courier/orders/" + id;
            }

            order.setStatus(StatusOrder.COMPLETED);

            // Update seller balance based on weight * 1000
            try {
                Seller seller = order.getSeller();
                if (seller != null && order.getTrash() != null) {
                    Double wObj = order.getTrash().getTrashWeight();
                    double w = (wObj != null && wObj > 0) ? wObj : 0.0;
                    if (w > 0) {
                        double increment = w * 1000.0;
                        Double current = seller.getBalance() != null ? seller.getBalance() : 0.0;
                        seller.setBalance(current + increment);
                        sellerRepository.save(seller);
                    }
                }
            } catch (Exception ignore) {
            }

            trashOrderRepository.save(order);
            redirectAttributes.addFlashAttribute("success", "Pesanan berhasil diselesaikan");
            return "redirect:/courier/orders";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/courier/orders/" + id;
        }
    }

    @PostMapping("/orders/{id}/reject")
    public String rejectOrder(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String detail,
            RedirectAttributes redirectAttributes) {
        if (!isCourierAuthenticated(request)) {
            return "redirect:/auth/courier/login";
        }

        Integer userId = SessionHelper.getUserId(request, "COURIER");

        try {
            Optional<TrashOrder> orderOpt = trashOrderRepository.findById(id.intValue());
            if (orderOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Order tidak ditemukan");
                return "redirect:/courier/orders";
            }

            TrashOrder order = orderOpt.get();
            if (order.getCourier() == null || !order.getCourier().getUser().getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "Order tidak dimiliki courier ini");
                return "redirect:/courier/orders";
            }

            if (order.getStatus() != StatusOrder.PENDING) {
                redirectAttributes.addFlashAttribute("error", "Hanya order dengan status PENDING yang bisa ditolak");
                return "redirect:/courier/orders/" + id;
            }

            if (reason == null || reason.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Alasan penolakan harus diisi");
                return "redirect:/courier/orders/" + id;
            }

            order.setStatus(StatusOrder.CANCELED);
            // Optionally, you can store the reason somewhere, e.g., in a new field or log it
            trashOrderRepository.save(order);
            redirectAttributes.addFlashAttribute("success", "Pesanan berhasil ditolak");
            return "redirect:/courier/orders";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/courier/orders/" + id;
        }
    }
}
