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
}
