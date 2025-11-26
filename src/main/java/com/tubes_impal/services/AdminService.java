package com.tubes_impal.services;

import com.tubes_impal.repos.CourierRepository;
import com.tubes_impal.repos.SellerRepository;
import com.tubes_impal.repos.TrashOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private TrashOrderRepository trashOrderRepository;

    /**
     * Get dashboard statistics
     * 
     * @return Map containing dashboard statistics
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total Seller
        long totalSellers = sellerRepository.count();
        stats.put("totalSellers", totalSellers);

        // Total Courier
        long totalCouriers = courierRepository.count();
        stats.put("totalCouriers", totalCouriers);

        // Total Order hari ini
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        long ordersToday = trashOrderRepository.findByTimeBetween(startOfDay, endOfDay).size();
        stats.put("totalOrdersToday", ordersToday);

        // Total berat Trash hari ini
        double totalWeight = trashOrderRepository.findByTimeBetween(startOfDay, endOfDay)
                .stream()
                .mapToDouble(order -> order.getTrash() != null ? order.getTrash().getTrashWeight() : 0.0)
                .sum();
        stats.put("totalTrashWeight", String.format("%.2f kg", totalWeight));

        return stats;
    }
}
