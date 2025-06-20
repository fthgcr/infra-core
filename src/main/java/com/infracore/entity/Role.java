package com.infracore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
    
    public enum RoleName {
        ADMIN,           // Sistem yöneticisi - her projede kullanılabilir
        USER,            // Standart kullanıcı - her projede kullanılabilir
        MANAGER,         // Yönetici - her projede kullanılabilir
        EMPLOYEE,        // Çalışan - her projede kullanılabilir
        CLIENT,          // Müşteri/Kullanıcı - her projede kullanılabilir
        GUEST            // Misafir kullanıcı - her projede kullanılabilir
    }
} 