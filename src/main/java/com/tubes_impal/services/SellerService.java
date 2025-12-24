package com.tubes_impal.services;

import com.tubes_impal.entity.Seller;
import com.tubes_impal.entity.StatusOrder;
import com.tubes_impal.entity.Trash;
import com.tubes_impal.entity.TrashOrder;
import com.tubes_impal.repos.SellerRepository;
import com.tubes_impal.repos.TrashOrderRepository;
import com.tubes_impal.repos.TrashRepository;
import com.tubes_impal.repos.UserRepository;
import com.tubes_impal.repos.ContactRepository;
import com.tubes_impal.entity.Contact;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final TrashOrderRepository trashOrderRepository;
    private final TrashRepository trashRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    public SellerService(SellerRepository sellerRepository,
            TrashOrderRepository trashOrderRepository,
            TrashRepository trashRepository,
            UserRepository userRepository,
            ContactRepository contactRepository) {
        this.sellerRepository = sellerRepository;
        this.trashOrderRepository = trashOrderRepository;
        this.trashRepository = trashRepository;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    public Seller getSellerByUserId(Integer userId) {
        return sellerRepository.findByUserId(userId)
                .orElseGet(() -> userRepository.findById(userId)
                        .map(user -> {
                            Seller seller = new Seller();
                            seller.setUser(user);
                            seller.setBalance(0.0);
                            return sellerRepository.save(seller);
                        })
                        .orElseThrow(() -> new IllegalArgumentException("Seller tidak ditemukan untuk user ini")));
    }

    public Map<String, Object> getDashboardData(Integer userId) {
        Seller seller = getSellerByUserId(userId);

        long totalOrders = trashOrderRepository.countBySellerId(seller.getId());
        String totalTransactions = totalOrders + " transaksi";

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        String currentBalance = "Rp " + currencyFormat.format(seller.getBalance() == null ? 0 : seller.getBalance());

        List<TrashOrder> allOrders = trashOrderRepository.findBySellerId(seller.getId());
        TrashOrder lastOrder = allOrders.stream()
                .filter(o -> o.getTime() != null)
                .max(Comparator.comparing(TrashOrder::getTime))
                .orElse(null);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("sellerName", seller.getUser().getName());
        dashboard.put("totalTransactions", totalTransactions);
        dashboard.put("currentBalance", currentBalance);
        dashboard.put("lastOrder", lastOrder != null ? toOrderView(lastOrder) : null);
        dashboard.put("errorLoading", false);
        return dashboard;
    }

    public List<Map<String, Object>> getOrders(Integer userId) {
        Seller seller = getSellerByUserId(userId);
        return trashOrderRepository.findBySellerId(seller.getId()).stream()
                .sorted(Comparator.comparing(TrashOrder::getTime).reversed())
                .map(this::toOrderView)
                .toList();
    }

    public Map<String, Object> getOrderDetail(Integer userId, Long orderId) {
        Seller seller = getSellerByUserId(userId);
        Optional<TrashOrder> orderOpt = trashOrderRepository.findById(orderId.intValue());

        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order tidak ditemukan");
        }

        TrashOrder order = orderOpt.get();
        if (!order.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException("Order tidak dimiliki seller ini");
        }

        return toOrderView(order);
    }

    @Transactional
    public void submitTrash(Integer userId, MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("File tidak dipilih. Silakan pilih gambar terlebih dahulu.");
        }

        if (!isSupportedContentType(imageFile.getContentType())) {
            throw new IllegalArgumentException("Format file tidak didukung. Gunakan JPEG atau PNG.");
        }

        long maxSize = 10 * 1024 * 1024;
        if (imageFile.getSize() > maxSize) {
            throw new IllegalArgumentException("File terlalu besar. Ukuran maksimal adalah 10 MB.");
        }

        // Pastikan profil kontak lengkap (email dan alamat)
        Contact contact = contactRepository.findByUserId(userId).orElse(null);
        if (contact == null || isBlank(contact.getEmail()) || isBlank(contact.getAddress())) {
            throw new IllegalArgumentException(
                    "Profil belum lengkap. Lengkapi email dan alamat di halaman Edit Profil.");
        }

        Seller seller = getSellerByUserId(userId);

        String storedPath = storeImage(imageFile);

        Trash trash = new Trash();
        trash.setPhotoProof(storedPath);
        trash.setTrashWeight(0.0);
        trashRepository.save(trash);

        TrashOrder order = new TrashOrder();
        order.setTrash(trash);
        order.setSeller(seller);
        order.setTime(LocalDateTime.now());
        order.setStatus(StatusOrder.PENDING);
        trashOrderRepository.save(order);
    }

    private Map<String, Object> toOrderView(TrashOrder order) {
        Map<String, Object> view = new HashMap<>();
        view.put("id", order.getId());
        view.put("orderId", order.getId() != null ? String.format("TRX-%05d", order.getId()) : "-");
        view.put("createdAt", formatDate(order.getTime()));
        view.put("weight", order.getTrash() != null && order.getTrash().getTrashWeight() != null
                ? order.getTrash().getTrashWeight()
                : 0);
        view.put("courierName", order.getCourier() != null && order.getCourier().getUser() != null
                ? order.getCourier().getUser().getName()
                : "Belum ditugaskan");
        view.put("amount", 0);
        view.put("description", order.getReasonDetail() != null ? order.getReasonDetail() : "-");
        view.put("status", order.getStatus() != null ? order.getStatus().name() : StatusOrder.PENDING.name());
        return view;
    }

    private String formatDate(LocalDateTime time) {
        if (time == null) {
            return "-";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return formatter.format(time);
    }

    private String storeImage(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        String newFileName = UUID.randomUUID() + extension;

        // Use absolute path from user.dir (project root)
        String uploadBasePath = System.getProperty("user.dir");
        Path uploadDir = Paths.get(uploadBasePath, "uploads", "trash");
        Files.createDirectories(uploadDir);

        Path destination = uploadDir.resolve(newFileName);
        file.transferTo(destination.toFile());

        return "/uploads/trash/" + newFileName;
    }

    private String getExtension(String filename) {
        if (filename == null) {
            return ".jpg";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return ".jpg";
        }
        return filename.substring(dotIndex);
    }

    private boolean isSupportedContentType(String contentType) {
        return "image/jpeg".equalsIgnoreCase(contentType) || "image/png".equalsIgnoreCase(contentType);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
