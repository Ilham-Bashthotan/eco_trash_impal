package com.tubes_impal.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "sellers")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Double balance = 0.0;

    @OneToMany(mappedBy = "seller")
    private List<TrashOrder> trashOrders;

    public Seller() {
    }

    public Seller(Integer id, User user, Double balance, List<TrashOrder> trashOrders) {
        this.id = id;
        this.user = user;
        this.balance = balance;
        this.trashOrders = trashOrders;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public List<TrashOrder> getTrashOrders() {
        return trashOrders;
    }

    public void setTrashOrders(List<TrashOrder> trashOrders) {
        this.trashOrders = trashOrders;
    }
}
