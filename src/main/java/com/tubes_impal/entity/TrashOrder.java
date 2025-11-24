package com.tubes_impal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trash_orders")
public class TrashOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "trash_id", nullable = false)
    private Trash trash;
    
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;
    
    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Courier courier;
    
    @Column(nullable = false)
    private LocalDateTime time;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrder status = StatusOrder.PENDING;
    
    @Column(nullable = true)
    private String reason;
    
    @Column(columnDefinition = "TEXT", nullable = true)
    private String reasonDetail;
    
    public TrashOrder() {
    }
    
    public TrashOrder(Integer id, Trash trash, Seller seller, Courier courier, LocalDateTime time, StatusOrder status, String reason, String reasonDetail) {
        this.id = id;
        this.trash = trash;
        this.seller = seller;
        this.courier = courier;
        this.time = time;
        this.status = status;
        this.reason = reason;
        this.reasonDetail = reasonDetail;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Trash getTrash() {
        return trash;
    }
    
    public void setTrash(Trash trash) {
        this.trash = trash;
    }
    
    public Seller getSeller() {
        return seller;
    }
    
    public void setSeller(Seller seller) {
        this.seller = seller;
    }
    
    public Courier getCourier() {
        return courier;
    }
    
    public void setCourier(Courier courier) {
        this.courier = courier;
    }
    
    public LocalDateTime getTime() {
        return time;
    }
    
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    
    public StatusOrder getStatus() {
        return status;
    }
    
    public void setStatus(StatusOrder status) {
        this.status = status;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getReasonDetail() {
        return reasonDetail;
    }
    
    public void setReasonDetail(String reasonDetail) {
        this.reasonDetail = reasonDetail;
    }
}
