package com.tubes_impal.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "couriers")
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "hire_by")
    private Admin hiredBy;

    @Column(name = "max_visits_a_day")
    private Integer maxVisitsADay;

    private Integer age;

    @Column(name = "driving_licence")
    private String drivingLicence;

    @Column(name = "id_card")
    private String idCard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCourier status = StatusCourier.AVAILABLE;

    @Column(name = "successful_deliveries")
    private Integer successfulDeliveries = 0;

    @Column(name = "failed_deliveries")
    private Integer failedDeliveries = 0;

    @OneToMany(mappedBy = "courier")
    private List<TrashOrder> trashOrders;

    @OneToMany(mappedBy = "courier")
    private List<CourierDailyStats> dailyStats;

    public Courier() {
    }

    public Courier(Integer id, User user, Admin hiredBy, Integer maxVisitsADay, Integer age, String drivingLicence,
            String idCard, StatusCourier status, Integer successfulDeliveries, Integer failedDeliveries,
            List<TrashOrder> trashOrders, List<CourierDailyStats> dailyStats) {
        this.id = id;
        this.user = user;
        this.hiredBy = hiredBy;
        this.maxVisitsADay = maxVisitsADay;
        this.age = age;
        this.drivingLicence = drivingLicence;
        this.idCard = idCard;
        this.status = status;
        this.successfulDeliveries = successfulDeliveries;
        this.failedDeliveries = failedDeliveries;
        this.trashOrders = trashOrders;
        this.dailyStats = dailyStats;
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

    public Admin getHiredBy() {
        return hiredBy;
    }

    public void setHiredBy(Admin hiredBy) {
        this.hiredBy = hiredBy;
    }

    public Integer getMaxVisitsADay() {
        return maxVisitsADay;
    }

    public void setMaxVisitsADay(Integer maxVisitsADay) {
        this.maxVisitsADay = maxVisitsADay;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDrivingLicence() {
        return drivingLicence;
    }

    public void setDrivingLicence(String drivingLicence) {
        this.drivingLicence = drivingLicence;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public StatusCourier getStatus() {
        return status;
    }

    public void setStatus(StatusCourier status) {
        this.status = status;
    }

    public Integer getSuccessfulDeliveries() {
        return successfulDeliveries;
    }

    public void setSuccessfulDeliveries(Integer successfulDeliveries) {
        this.successfulDeliveries = successfulDeliveries;
    }

    public Integer getFailedDeliveries() {
        return failedDeliveries;
    }

    public void setFailedDeliveries(Integer failedDeliveries) {
        this.failedDeliveries = failedDeliveries;
    }

    public List<TrashOrder> getTrashOrders() {
        return trashOrders;
    }

    public void setTrashOrders(List<TrashOrder> trashOrders) {
        this.trashOrders = trashOrders;
    }

    public List<CourierDailyStats> getDailyStats() {
        return dailyStats;
    }

    public void setDailyStats(List<CourierDailyStats> dailyStats) {
        this.dailyStats = dailyStats;
    }
}
