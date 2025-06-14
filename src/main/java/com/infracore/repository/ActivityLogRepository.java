package com.infracore.repository;

import com.infracore.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /**
     * Find recent activities ordered by creation date
     */
    List<ActivityLog> findAllByOrderByCreatedDateDesc();

    /**
     * Find activities by user
     */
    List<ActivityLog> findByPerformedByIdOrderByCreatedDateDesc(Long userId);

    /**
     * Find activities by entity type
     */
    List<ActivityLog> findByTargetEntityTypeOrderByCreatedDateDesc(ActivityLog.EntityType entityType);

    /**
     * Find activities within date range
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.createdDate >= :startDate AND a.createdDate <= :endDate ORDER BY a.createdDate DESC")
    List<ActivityLog> findActivitiesInDateRange(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * Find activities by target entity
     */
    List<ActivityLog> findByTargetEntityIdAndTargetEntityTypeOrderByCreatedDateDesc(Long entityId, ActivityLog.EntityType entityType);
} 