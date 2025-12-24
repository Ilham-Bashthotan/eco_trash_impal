package com.tubes_impal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;

@Controller
@RequestMapping("/courier")
public class CourierController {

    // Helper method to create dummy orders list
    private List<Map<String, Object>> getDummyOrders() {
        List<Map<String, Object>> orders = new ArrayList<>();

        Map<String, Object> order1 = new HashMap<>();
        order1.put("id", 1);
        order1.put("orderId", "ORD-1001");
        order1.put("sellerName", "Alice Shop");
        order1.put("status", "PENDING");
        order1.put("weight", "2.5");
        order1.put("address", "Jl. Merdeka 12, Bandung");
        order1.put("createdAt", "2025-11-24 09:45");
        order1.put("description", "Sampah plastik dan kertas");
        order1.put("latitude", -6.9271);
        order1.put("longitude", 107.6411);
        order1.put("photoProof", null);
        order1.put("reason", null);
        order1.put("reasonDetail", null);
        orders.add(order1);

        Map<String, Object> order2 = new HashMap<>();
        order2.put("id", 2);
        order2.put("orderId", "ORD-1002");
        order2.put("sellerName", "Bob Store");
        order2.put("status", "PICKED_UP");
        order2.put("weight", "1.8");
        order2.put("address", "Jl. Sudirman No. 456, Jakarta");
        order2.put("createdAt", "2025-12-06 10:30");
        order2.put("description", "Sampah elektronik");
        order2.put("latitude", -6.2088);
        order2.put("longitude", 106.8456);
        order2.put("photoProof", null);
        order2.put("reason", null);
        order2.put("reasonDetail", null);
        orders.add(order2);

        Map<String, Object> order3 = new HashMap<>();
        order3.put("id", 3);
        order3.put("orderId", "ORD-1003");
        order3.put("sellerName", "Charlie Market");
        order3.put("status", "DELIVERED");
        order3.put("weight", "3.2");
        order3.put("address", "Jl. Gatot Subroto No. 789, Jakarta");
        order3.put("createdAt", "2025-12-06 11:45");
        order3.put("description", "Sampah organik dan limbah");
        order3.put("latitude", -6.2297);
        order3.put("longitude", 106.7837);
        order3.put("photoProof", null);
        order3.put("reason", null);
        order3.put("reasonDetail", null);
        orders.add(order3);

        Map<String, Object> order4 = new HashMap<>();
        order4.put("id", 4);
        order4.put("orderId", "ORD-1004");
        order4.put("sellerName", "Diana Cafe");
        order4.put("status", "COMPLETED");
        order4.put("weight", "1.5");
        order4.put("address", "Jl. Thamrin No. 321, Jakarta");
        order4.put("createdAt", "2025-12-05 14:20");
        order4.put("description", "Sampah sisa makanan");
        order4.put("latitude", -6.1944);
        order4.put("longitude", 106.8296);
        order4.put("photoProof", null);
        order4.put("reason", null);
        order4.put("reasonDetail", null);
        orders.add(order4);

        Map<String, Object> order5 = new HashMap<>();
        order5.put("id", 5);
        order5.put("orderId", "ORD-1005");
        order5.put("sellerName", "Evan's Store");
        order5.put("status", "CANCELLED");
        order5.put("weight", "0.9");
        order5.put("address", "Jl. Ahmad Yani No. 555, Jakarta");
        order5.put("createdAt", "2025-12-05 16:00");
        order5.put("description", "Sampah logam bekas");
        order5.put("latitude", -6.1753);
        order5.put("longitude", 106.8249);
        order5.put("photoProof", null);
        order5.put("reason", "LOKASI_BERBAHAYA");
        order5.put("reasonDetail", "Lokasi terletak di area yang berbahaya untuk dijangkau");
        orders.add(order5);

        return orders;
    }

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/courier/dashboard";
    }

    @GetMapping("/dashboard")
    public String CourierDashboard(Model model) {
        // Dummy data untuk courier
        model.addAttribute("courierName", "Budi Santoso");

        // Dummy stats
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCompleted", 42);
        stats.put("totalWeight", "156.75");
        stats.put("totalPhotoOrders", 18);
        model.addAttribute("stats", stats);

        // Dummy active orders (filtered)
        List<Map<String, Object>> allOrders = getDummyOrders();
        List<Map<String, Object>> activeOrders = new ArrayList<>();
        for (Map<String, Object> order : allOrders) {
            String status = (String) order.get("status");
            if (status.equals("PENDING") || status.equals("PICKED_UP")) {
                activeOrders.add(order);
            }
        }

        model.addAttribute("activeOrders", activeOrders);

        return "courier/courier-dashboard";
    }

    @GetMapping("/orders")
    public String CourierOrders(Model model) {
        model.addAttribute("courierName", "Budi Santoso");
        List<Map<String, Object>> allOrders = getDummyOrders();
        model.addAttribute("orders", allOrders);
        return "courier/courier-orders";
    }

    @GetMapping("/orders/{id}")
    public String CourierOrderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("courierName", "Budi Santoso");

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
            order.put("orderId", "ORD-NOT-FOUND");
            order.put("sellerName", "Unknown");
            order.put("status", "UNKNOWN");
            order.put("weight", "0");
            order.put("address", "Unknown Address");
            order.put("createdAt", "N/A");
            order.put("description", "Order tidak ditemukan");
        }

        model.addAttribute("order", order);
        return "courier/courier-order-detail";
    }
}
