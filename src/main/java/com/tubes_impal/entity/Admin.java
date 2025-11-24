package com.tubes_impal.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "admins")
public class Admin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @OneToMany(mappedBy = "hiredBy")
    private List<Courier> hiredCouriers;
    
    public Admin() {
    }
    
    public Admin(Integer id, User user, List<Courier> hiredCouriers) {
        this.id = id;
        this.user = user;
        this.hiredCouriers = hiredCouriers;
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
    
    public List<Courier> getHiredCouriers() {
        return hiredCouriers;
    }
    
    public void setHiredCouriers(List<Courier> hiredCouriers) {
        this.hiredCouriers = hiredCouriers;
    }
}
