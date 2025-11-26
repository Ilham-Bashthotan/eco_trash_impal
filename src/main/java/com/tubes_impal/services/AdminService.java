package com.tubes_impal.services;

import com.tubes_impal.entity.Courier;
import com.tubes_impal.entity.StatusCourier;
import com.tubes_impal.repos.CourierRepository;
import com.tubes_impal.repos.SellerRepository;
import com.tubes_impal.repos.TrashOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    /**
     * Get courier management data
     * 
     * @return Map containing calon kurir and kurir aktif
     */
    public Map<String, Object> getCourierManagementData() {
        Map<String, Object> data = new HashMap<>();

        // Calon Kurir (couriers with OFFLINE status - not yet hired)
        List<Courier> calonKurir = courierRepository.findByStatus(StatusCourier.OFFLINE);
        data.put("calonKurir", calonKurir);

        // Kurir Aktif (couriers with AVAILABLE, ON_DELIVERY, or UNAVAILABLE status)
        List<Courier> kurirAktif = courierRepository.findAll().stream()
                .filter(c -> c.getStatus() == StatusCourier.AVAILABLE ||
                        c.getStatus() == StatusCourier.ON_DELIVERY ||
                        c.getStatus() == StatusCourier.UNAVAILABLE)
                .toList();
        data.put("kurirAktif", kurirAktif);

        return data;
    }

    /**
     * Hire a courier (change status from OFFLINE to AVAILABLE)
     * 
     * @param courierId Courier ID
     * @return true if successful, false otherwise
     */
    public boolean hireCourier(Integer courierId) {
        Optional<Courier> courierOpt = courierRepository.findById(courierId);
        if (courierOpt.isPresent()) {
            Courier courier = courierOpt.get();
            if (courier.getStatus() == StatusCourier.OFFLINE) {
                courier.setStatus(StatusCourier.AVAILABLE);
                courierRepository.save(courier);
                return true;
            }
        }
        return false;
    }

    /**
     * Fire a courier (change status to OFFLINE)
     * 
     * @param courierId Courier ID
     * @return true if successful, false otherwise
     */
    public boolean fireCourier(Integer courierId) {
        Optional<Courier> courierOpt = courierRepository.findById(courierId);
        if (courierOpt.isPresent()) {
            Courier courier = courierOpt.get();
            if (courier.getStatus() != StatusCourier.OFFLINE) {
                courier.setStatus(StatusCourier.OFFLINE);
                courierRepository.save(courier);
                return true;
            }
        }
        return false;
    }

    /**
     * Get trash statistics for today
     * 
     * @return Map containing trash statistics
     */
    public Map<String, Object> getTrashStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Get today's date range
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        // Get all trash orders for today
        var ordersToday = trashOrderRepository.findByTimeBetween(startOfDay, endOfDay);
        stats.put("trashOrders", ordersToday);

        // Total orders today
        stats.put("totalOrders", ordersToday.size());

        // Total weight today
        double totalWeight = ordersToday.stream()
                .mapToDouble(order -> order.getTrash() != null ? order.getTrash().getTrashWeight() : 0.0)
                .sum();
        stats.put("totalWeight", String.format("%.2f kg", totalWeight));

        // Average weight per order
        double averageWeight = ordersToday.isEmpty() ? 0.0 : totalWeight / ordersToday.size();
        stats.put("averageWeight", String.format("%.2f kg", averageWeight));

        return stats;
    }

    /**
     * Get courier statistics
     * 
     * @return Map containing courier statistics
     */
    public Map<String, Object> getCourierStatistics() {
        Map<String, Object> stats = new HashMap<>();
        List<Map<String, Object>> courierStats = new java.util.ArrayList<>();

        // Get all active couriers (not OFFLINE)
        List<Courier> activeCouriers = courierRepository.findAll().stream()
                .filter(c -> c.getStatus() != StatusCourier.OFFLINE)
                .toList();

        int totalDeliveries = 0;
        int totalOnTime = 0;
        int totalLate = 0;

        // Calculate stats for each courier
        for (Courier courier : activeCouriers) {
            Map<String, Object> courierStat = new HashMap<>();

            // Get all orders for this courier
            var orders = trashOrderRepository.findAll().stream()
                    .filter(o -> o.getCourier() != null && o.getCourier().getId().equals(courier.getId()))
                    .toList();

            int deliveries = orders.size();
            // Simulate on-time and late deliveries (you can implement real logic based on
            // your business rules)
            // For now, assuming 80% are on time
            int onTime = (int) (deliveries * 0.8);
            int late = deliveries - onTime;

            double successRate = deliveries > 0 ? (double) onTime / deliveries * 100 : 0;

            courierStat.put("courierName", courier.getUser().getName());
            courierStat.put("totalDeliveries", deliveries);
            courierStat.put("onTime", onTime);
            courierStat.put("late", late);
            courierStat.put("successRate", String.format("%.1f%%", successRate));
            courierStat.put("successRateValue", successRate);
            courierStat.put("status", courier.getStatus().name());

            courierStats.add(courierStat);

            totalDeliveries += deliveries;
            totalOnTime += onTime;
            totalLate += late;
        }

        stats.put("courierStats", courierStats);
        stats.put("totalActiveCouriers", activeCouriers.size());
        stats.put("totalDeliveries", totalDeliveries);
        stats.put("totalOnTime", totalOnTime);
        stats.put("totalLate", totalLate);

        return stats;
    }
}
