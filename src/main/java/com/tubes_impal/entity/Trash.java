package com.tubes_impal.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "trash")
public class Trash {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "photo_proof")
    private String photoProof;
    
    @Column(name = "trash_weight")
    private Double trashWeight;
    
    @OneToMany(mappedBy = "trash")
    private List<TrashOrder> trashOrders;
    
    public Trash() {
    }
    
    public Trash(Integer id, String address, String photoProof, Double trashWeight, List<TrashOrder> trashOrders) {
        this.id = id;
        this.address = address;
        this.photoProof = photoProof;
        this.trashWeight = trashWeight;
        this.trashOrders = trashOrders;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhotoProof() {
        return photoProof;
    }
    
    public void setPhotoProof(String photoProof) {
        this.photoProof = photoProof;
    }
    
    public Double getTrashWeight() {
        return trashWeight;
    }
    
    public void setTrashWeight(Double trashWeight) {
        this.trashWeight = trashWeight;
    }
    
    public List<TrashOrder> getTrashOrders() {
        return trashOrders;
    }
    
    public void setTrashOrders(List<TrashOrder> trashOrders) {
        this.trashOrders = trashOrders;
    }
}
