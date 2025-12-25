package com.tubes_impal.controller;

import com.tubes_impal.services.SellerService;
import com.tubes_impal.repos.ContactRepository;
import com.tubes_impal.entity.Contact;
import com.tubes_impal.repos.UserRepository;
import com.tubes_impal.entity.User;
import com.tubes_impal.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seller")
public class SellerController {

    private final SellerService sellerService;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public SellerController(SellerService sellerService,
            ContactRepository contactRepository,
            UserRepository userRepository) {
        this.sellerService = sellerService;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    private Integer requireSeller(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!SessionHelper.isAuthenticated(request, "SELLER")) {
            redirectAttributes.addFlashAttribute("error", "Silakan login sebagai seller terlebih dahulu");
            return null;
        }
        return SessionHelper.getUserId(request, "SELLER");
    }

    @GetMapping("/")
    public String SellerDashboard(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Integer userId = requireSeller(request, redirectAttributes);
        if (userId == null) {
            return "redirect:/auth/seller/login";
        }

        try {
            Map<String, Object> dashboard = sellerService.getDashboardData(userId);
            model.addAttribute("sellerName", dashboard.get("sellerName"));
            model.addAttribute("totalTransactions", dashboard.get("totalTransactions"));
            model.addAttribute("currentBalance", dashboard.get("currentBalance"));
            model.addAttribute("lastOrder", dashboard.get("lastOrder"));
            model.addAttribute("errorLoading", dashboard.get("errorLoading"));
        } catch (Exception e) {
            model.addAttribute("errorLoading", true);
            model.addAttribute("sellerName", "Seller");
        }

        return "seller/seller-dashboard";
    }

    @GetMapping("/dashboard")
    public String SellerDashboardAlternative(Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        return SellerDashboard(model, request, redirectAttributes);
    }

    @GetMapping("/orders")
    public String SellerOrders(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Integer userId = requireSeller(request, redirectAttributes);
        if (userId == null) {
            return "redirect:/auth/seller/login";
        }

        List<Map<String, Object>> orders = sellerService.getOrders(userId);
        model.addAttribute("orders", orders);
        model.addAttribute("sellerName", SessionHelper.getUsername(request, "SELLER"));
        return "seller/seller-riwayat-orders";
    }

    @GetMapping("/orders/{id}")
    public String SellerOrderDetail(@PathVariable Long id,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        Integer userId = requireSeller(request, redirectAttributes);
        if (userId == null) {
            return "redirect:/auth/seller/login";
        }

        try {
            Map<String, Object> order = sellerService.getOrderDetail(userId, id);
            model.addAttribute("order", order);
            model.addAttribute("sellerName", SessionHelper.getUsername(request, "SELLER"));
            return "seller/seller-rincian-orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/seller/orders";
        }
    }

    @GetMapping("/profile")
    public String SellerProfile(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Integer userId = requireSeller(request, redirectAttributes);
        if (userId == null) {
            return "redirect:/auth/seller/login";
        }
        Contact contact = contactRepository.findByUserId(userId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("sellerName", user != null ? user.getName() : SessionHelper.getUsername(request, "SELLER"));
        model.addAttribute("contact", contact);
        return "seller/seller-profile";
    }

    @GetMapping("/profile/edit")
    public String SellerProfileEdit(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Integer userId = requireSeller(request, redirectAttributes);
        if (userId == null) {
            return "redirect:/auth/seller/login";
        }
        Contact contact = contactRepository.findByUserId(userId).orElseGet(() -> {
            Contact c = new Contact();
            User u = userRepository.findById(userId).orElse(null);
            c.setUser(u);
            return c;
        });
        model.addAttribute("contact", contact);
        model.addAttribute("sellerName", SessionHelper.getUsername(request, "SELLER"));
        return "seller/seller-profile-edit";
    }

    @PostMapping("/profile/edit")
    public String SellerProfileEditSubmit(@RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        Integer userId = requireSeller(request, redirectAttributes);
        if (userId == null) {
            return "redirect:/auth/seller/login";
        }

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
        contact.setLatitude(latitude);
        contact.setLongitude(longitude);

        contactRepository.save(contact);
        redirectAttributes.addFlashAttribute("success", "Profil berhasil diperbarui");
        return "redirect:/seller/profile";
    }

    @GetMapping("/kirim-sampah")
    public String SellerKirimSampah(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Integer userId = requireSeller(request, redirectAttributes);
        if (userId == null) {
            return "redirect:/auth/seller/login";
        }

        // Cek kelengkapan profil sebelum menampilkan form
        Contact contact = contactRepository.findByUserId(userId).orElse(null);
        if (contact == null || isBlank(contact.getEmail()) || isBlank(contact.getAddress())) {
            redirectAttributes.addFlashAttribute("error",
                    "Profil belum lengkap. Harap lengkapi email dan alamat terlebih dahulu sebelum mengirim sampah.");
            return "redirect:/seller/profile/edit";
        }

        model.addAttribute("sellerName", SessionHelper.getUsername(request, "SELLER"));
        return "seller/seller-kirim-sampah";
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @PostMapping("/kirim-sampah")
    public String SellerKirimSampahSubmit(@RequestParam("imageFile") MultipartFile imageFile,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        Integer userId = requireSeller(request, redirectAttributes);
        if (userId == null) {
            return "redirect:/auth/seller/login";
        }

        try {
            var order = sellerService.submitTrash(userId, imageFile);
            if (order.getStatus() == com.tubes_impal.entity.StatusOrder.CANCELED) {
                redirectAttributes.addFlashAttribute("error",
                        "Order dibatalkan otomatis: " + (order.getReasonDetail() != null ? order.getReasonDetail()
                                : "Tidak ada kurir tersedia"));
            } else {
                redirectAttributes.addFlashAttribute("success",
                        "Gambar berhasil dikirim dan order dibuat!" +
                                (imageFile.getOriginalFilename() != null ? " File: " + imageFile.getOriginalFilename()
                                        : ""));
            }
            return "redirect:/seller/orders";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/seller/kirim-sampah";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menyimpan file: " + e.getMessage());
            return "redirect:/seller/kirim-sampah";
        }
    }
}
