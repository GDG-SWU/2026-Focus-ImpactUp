package com.example.focus.domain.map.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "facilities")
@Getter
@NoArgsConstructor
public class Facility {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String name; // 기관명

    @Column(nullable = false, length = 20)
    private String category; // camp, hospital, ngo, water

    @Column(nullable = false)
    private double latitude; // 위도

    @Column(nullable = false)
    private double longitude; // 경도

    @Column(nullable = false, length = 20)
    private String availability; // available, crowded, unavailable

    @Column(nullable = false)
    private boolean operating; // 운영 여부

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Builder
    public Facility(String name, String category, double latitude, double longitude, String availability, boolean operating) {
        this.name = name;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availability = availability;
        this.operating = operating;
    }
}