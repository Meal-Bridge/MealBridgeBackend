package com.meal_bridge.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.meal_bridge.models.enums.AvailabilityStatus;
import com.meal_bridge.models.enums.MessType;
import com.meal_bridge.models.enums.ServiceType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "mess")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mess implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String ownerName;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    private Address address;

    @Column(nullable = false, length = 15, unique = true)
    private String contactNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private Integer capacity;

    @Column(nullable = true)
    private Integer currentMembers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus availabilityStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private String photoUrl;

    @Column(length = 500)
    private String description;

    @ElementCollection(targetClass = ServiceType.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "mess_service_types", joinColumns = @JoinColumn(name = "mess_id"))
    @Column(name = "service_type")
    private List<ServiceType> serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessType messType;

    @OneToMany(mappedBy = "mess",cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Client> clients;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
