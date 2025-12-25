package com.tubes_impal.services;

import com.tubes_impal.entity.Courier;
import com.tubes_impal.entity.TrashOrder;
import com.tubes_impal.entity.Contact;
import com.tubes_impal.repos.CourierRepository;
import com.tubes_impal.repos.TrashOrderRepository;
import com.tubes_impal.repos.UserRepository;
import com.tubes_impal.repos.ContactRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CourierService {

    private final CourierRepository courierRepository;
    private final TrashOrderRepository trashOrderRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    public CourierService(CourierRepository courierRepository,
            TrashOrderRepository trashOrderRepository,
            UserRepository userRepository,
            ContactRepository contactRepository) {
        this.courierRepository = courierRepository;
        this.trashOrderRepository = trashOrderRepository;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    public Courier getCourierByUserId(Integer userId) {
        return courierRepository.findByUserId(userId)
                .orElseGet(() -> userRepository.findById(userId)
                        .map(user -> {
                            Courier courier = new Courier();
                            courier.setUser(user);
                            return courierRepository.save(courier);
                        })
                        .orElseThrow(() -> new IllegalArgumentException("Courier tidak ditemukan untuk user ini")));
    }

    public Map<String, Object> getDashboardData(Integer userId) {
        Courier courier = getCourierByUserId(userId);

        // Get all orders for this courier
        List<TrashOrder> allOrders = trashOrderRepository.findByCourierId(courier.getId());

        // Count completed orders
        long totalCompleted = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus().name().equals("COMPLETED"))
                .count();

        // Calculate total weight
        double totalWeight = allOrders.stream()
                .mapToDouble(o -> o.getTrash() != null && o.getTrash().getTrashWeight() != null
                        ? o.getTrash().getTrashWeight()
                        : 0.0)
                .sum();

        // Count photo orders (orders with photo proof)
        long totalPhotoOrders = allOrders.stream()
                .filter(o -> o.getTrash() != null && o.getTrash().getPhotoProof() != null
                        && !o.getTrash().getPhotoProof().isEmpty())
                .count();

        // Get active orders (PENDING or PICKED_UP)
        List<Map<String, Object>> activeOrders = allOrders.stream()
                .filter(o -> o.getStatus() != null && (o.getStatus().name().equals("PENDING")
                        || o.getStatus().name().equals("PICKED_UP")))
                .sorted(Comparator.comparing(TrashOrder::getTime).reversed())
                .map(this::toOrderView)
                .toList();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("courierName", courier.getUser().getName());
        dashboard.put("stats", Map.of(
                "totalCompleted", totalCompleted,
                "totalWeight", String.format("%.2f", totalWeight),
                "totalPhotoOrders", totalPhotoOrders));
        dashboard.put("activeOrders", activeOrders);
        return dashboard;
    }

    public List<Map<String, Object>> getOrders(Integer userId) {
        Courier courier = getCourierByUserId(userId);
        return trashOrderRepository.findByCourierId(courier.getId()).stream()
                .sorted(Comparator.comparing(TrashOrder::getTime).reversed())
                .map(this::toOrderView)
                .toList();
    }

    public Map<String, Object> getOrderDetail(Integer userId, Long orderId) {
        Courier courier = getCourierByUserId(userId);
        Optional<TrashOrder> orderOpt = trashOrderRepository.findById(orderId.intValue());

        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order tidak ditemukan");
        }

        TrashOrder order = orderOpt.get();
        if (!order.getCourier().getId().equals(courier.getId())) {
            throw new IllegalArgumentException("Order tidak dimiliki courier ini");
        }

        return toOrderView(order);
    }

    private Map<String, Object> toOrderView(TrashOrder order) {
        Map<String, Object> view = new HashMap<>();
        view.put("id", order.getId());
        view.put("orderId", order.getId() != null ? String.format("TRX-%05d", order.getId()) : "-");
        view.put("sellerName", order.getSeller() != null && order.getSeller().getUser() != null
                ? order.getSeller().getUser().getName()
                : "Unknown");
        // Address should be taken from Seller's Contact.address via User relationship
        String address = "-";
        if (order.getSeller() != null && order.getSeller().getUser() != null
                && order.getSeller().getUser().getId() != null) {
            Integer sellerUserId = order.getSeller().getUser().getId();
            try {
                Optional<Contact> contactOpt = contactRepository.findByUserId(sellerUserId);
                if (contactOpt != null && contactOpt.isPresent() && contactOpt.get().getAddress() != null
                        && !contactOpt.get().getAddress().isEmpty()) {
                    address = contactOpt.get().getAddress();
                }
            } catch (Exception ignore) {
                // keep default address "-" if lookup fails
            }
        }
        view.put("address", address);
        view.put("weight", order.getTrash() != null && order.getTrash().getTrashWeight() != null
                ? order.getTrash().getTrashWeight()
                : 0);
        view.put("status", order.getStatus() != null ? order.getStatus().name() : "UNKNOWN");
        view.put("createdAt", formatDate(order.getTime()));
        view.put("description", order.getReasonDetail() != null ? order.getReasonDetail() : "-");
        view.put("photoProof",
                order.getTrash() != null ? order.getTrash().getPhotoProof() : null);
        view.put("reason", order.getReason() != null ? order.getReason() : null);
        view.put("reasonDetail", order.getReasonDetail() != null ? order.getReasonDetail() : null);
        return view;
    }

    private String formatDate(java.time.LocalDateTime time) {
        if (time == null) {
            return "-";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return formatter.format(time);
    }
}
