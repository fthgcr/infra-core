package com.infracore.repository;

import com.infracore.entity.Role;
import com.infracore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Role bazlı sorgular
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") Role.RoleName roleName);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.enabled = true")
    List<User> findActiveUsersByRoleName(@Param("roleName") Role.RoleName roleName);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name IN :roleNames")
    List<User> findByRoleNames(@Param("roleNames") List<Role.RoleName> roleNames);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name IN :roleNames AND u.enabled = true")
    List<User> findActiveUsersByRoleNames(@Param("roleNames") List<Role.RoleName> roleNames);
    
    // Admin kullanıcıları getir
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ADMIN'")
    List<User> findAllAdmins();
    
    // Client kullanıcıları getir (USER rolündeki kullanıcılar müvekkil olarak kabul ediliyor)
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'USER'")
    List<User> findAllClients();
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'USER' AND u.enabled = true")
    List<User> findActiveClients();
    
    // Belirli bir role sahip kullanıcı sayısı
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Long countByRoleName(@Param("roleName") Role.RoleName roleName);
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.enabled = true")
    Long countActiveByRoleName(@Param("roleName") Role.RoleName roleName);
}
