package com.meal_bridge.models.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.meal_bridge.models.enums.Gender;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mess_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessUser implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userId;

    @Column(nullable = false)
    @NotNull(message = "First name is required")
    private String firstName;

    private String middleName;

    @Column(nullable = false)
    @NotNull(message = "Last name is required")
    private String lastName;

    @Column(nullable = false, length = 512,unique = true)
    @Email(message = "Email should be valid")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Pattern(
        regexp = "^\\+?[0-9]{10,15}$", 
        message = "Phone number must be valid and between 10 to 15 digits"
    )
    private String phone;

    private LocalDate dob;

    private Integer age;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "salutation", nullable = true, length = 50)
    private String salutation;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mess_id", referencedColumnName = "id", nullable = true)
    private Mess mess;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = true)
    private Address address;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
