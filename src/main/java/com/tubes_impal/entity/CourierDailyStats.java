package com.tubes_impal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "courier_daily_stats")
public class CourierDailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer visits = 0;

    public CourierDailyStats() {
    }

    public CourierDailyStats(Integer id, Courier courier, LocalDate date, Integer visits) {
        this.id = id;
        this.courier = courier;
        this.date = date;
        this.visits = visits;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getVisits() {
        return visits;
    }

    public void setVisits(Integer visits) {
        this.visits = visits;
    }
}
