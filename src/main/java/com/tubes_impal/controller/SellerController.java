package com.tubes_impal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Controller
@RequestMapping("/seller")
public class SellerController {

    // Helper method to create dummy orders list
    private List<Map<String, Object>> getDummyOrders() {
        List<Map<String, Object>> orders = new ArrayList<>();

        Map<String, Object> order1 = new HashMap<>();
        order1.put("id", 1);
        order1.put("orderId", "TRX-2001");
        order1.put("courierName", "Budi Santoso");
        order1.put("status", "COMPLETED");
        order1.put("weight", "2.5");
        order1.put("amount", 50000);
        order1.put("createdAt", "2025-12-10 09:45");
        order1.put("description", "Sampah plastik dan kertas");
        order1.put("photoProof", "/images/proof-1.jpg");
        orders.add(order1);

        Map<String, Object> order2 = new HashMap<>();
        order2.put("id", 2);
        order2.put("orderId", "TRX-2002");
        order2.put("courierName", "Adi Wijaya");
        order2.put("status", "COMPLETED");
        order2.put("weight", "1.8");
        order2.put("amount", 36000);
        order2.put("createdAt", "2025-12-09 10:30");
        order2.put("description", "Sampah elektronik");
        order2.put("photoProof", "/images/proof-2.jpg");
        orders.add(order2);

        Map<String, Object> order3 = new HashMap<>();
        order3.put("id", 3);
        order3.put("orderId", "TRX-2003");
        order3.put("courierName", "Citra Dewi");
        order3.put("status", "PENDING");
        order3.put("weight", "3.2");
        order3.put("amount", 64000);
        order3.put("createdAt", "2025-12-08 11:45");
        order3.put("description", "Sampah organik dan limbah");
        order3.put("photoProof", null);
        orders.add(order3);

        Map<String, Object> order4 = new HashMap<>();
        order4.put("id", 4);
        order4.put("orderId", "TRX-2004");
        order4.put("courierName", "Doni Pratama");
        order4.put("status", "COMPLETED");
        order4.put("weight", "1.5");
        order4.put("amount", 30000);
        order4.put("createdAt", "2025-12-07 14:20");
        order4.put("description", "Sampah sisa makanan");
        order4.put("photoProof", "/images/proof-4.jpg");
        orders.add(order4);

        Map<String, Object> order5 = new HashMap<>();
        order5.put("id", 5);
        order5.put("orderId", "TRX-2005");
        order5.put("courierName", "Eka Sutrisna");
        order5.put("status", "COMPLETED");
        order5.put("weight", "0.9");
        order5.put("amount", 18000);
        order5.put("createdAt", "2025-12-06 16:00");
        order5.put("description", "Sampah logam bekas");
        order5.put("photoProof", "/images/proof-5.jpg");
        orders.add(order5);

        return orders;
    }

    @GetMapping("/")
    public String SellerDashboard(Model model) {
        // Dummy data untuk seller

        // Dummy stats
        model.addAttribute("totalTransactions", "128 transaksi");
        model.addAttribute("currentBalance", "Rp 1.250.000");

        // Dummy last order
        List<Map<String, Object>> allOrders = getDummyOrders();
        if (!allOrders.isEmpty()) {
            model.addAttribute("lastOrder", allOrders.get(0));
        }

        model.addAttribute("errorLoading", false);
        model.addAttribute("sellerName", "Seller");

        return "seller/seller-dashboard";
    }

    @GetMapping("/dashboard")
    public String SellerDashboardAlternative(Model model) {
        return SellerDashboard(model);
    }

    @GetMapping("/orders")
    public String SellerOrders(Model model) {
        List<Map<String, Object>> allOrders = getDummyOrders();
        model.addAttribute("orders", allOrders);
        model.addAttribute("sellerName", "Seller");
        return "seller/seller-riwayat-orders";
    }

    @GetMapping("/orders/{id}")
    public String SellerOrderDetail(@PathVariable Long id, Model model) {

        List<Map<String, Object>> allOrders = getDummyOrders();
        Map<String, Object> order = null;

        for (Map<String, Object> o : allOrders) {
            if (((Number) o.get("id")).longValue() == id) {
                order = o;
                break;
            }
        }

        if (order == null) {
            // Default order jika tidak ditemukan
            order = new HashMap<>();
            order.put("id", id);
            order.put("orderId", "TRX-NOT-FOUND");
            order.put("courierName", "Unknown");
            order.put("status", "UNKNOWN");
            order.put("weight", "0");
            order.put("amount", 0);
            order.put("createdAt", "N/A");
            order.put("description", "Order tidak ditemukan");
        }

        model.addAttribute("order", order);
        model.addAttribute("sellerName", "Seller");
        return "seller/seller-rincian-orders";
    }

    @GetMapping("/profile")
    public String SellerProfile(Model model) {
        model.addAttribute("sellerName", "Seller");
        model.addAttribute("sellerEmail", "alice@shop.com");
        model.addAttribute("sellerPhone", "08123456789");
        model.addAttribute("sellerAddress", "Jl. Merdeka No. 123, Bandung");
        model.addAttribute("totalEarning", "Rp 12.500.000");
        model.addAttribute("totalTransactions", "128");
        return "seller/seller-profile";
    }

    @GetMapping("/kirim-sampah")
    public String SellerKirimSampah(Model model) {
        model.addAttribute("sellerName", "Seller");
        return "seller/seller-kirim-sampah";
    }

    @PostMapping("/kirim-sampah")
    public String SellerKirimSampahSubmit(@RequestParam("imageFile") MultipartFile imageFile, Model model) {
        model.addAttribute("sellerName", "Seller");

        if (imageFile.isEmpty()) {
            model.addAttribute("errorMessage", "File tidak dipilih. Silakan pilih gambar terlebih dahulu.");
            return "seller/seller-kirim-sampah";
        }

        // Validasi ukuran file (max 10 MB)
        long maxSize = 10 * 1024 * 1024;
        if (imageFile.getSize() > maxSize) {
            model.addAttribute("errorMessage", "File terlalu besar. Ukuran maksimal adalah 10 MB.");
            return "seller/seller-kirim-sampah";
        }

        // Validasi tipe file
        String contentType = imageFile.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            model.addAttribute("errorMessage", "Format file tidak didukung. Gunakan JPEG atau PNG.");
            return "seller/seller-kirim-sampah";
        }

        // TODO: Simpan file ke storage dan proses pesanan
        // Untuk sekarang hanya tampilkan success message
        model.addAttribute("successMessage", "Gambar berhasil dikirim! File: " + imageFile.getOriginalFilename());

        return "seller/seller-kirim-sampah";
    }
}
