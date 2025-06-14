package com.infracore.service;

import com.infracore.entity.ActivityLog;
import com.infracore.entity.User;
import com.infracore.repository.ActivityLogRepository;
import com.infracore.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserService userService;

    /**
     * Log an activity
     */
    @Transactional
    public void logActivity(ActivityLog.ActivityType type, String description, 
                           Long targetEntityId, String targetEntityName, 
                           ActivityLog.EntityType targetEntityType) {
        logActivity(type, description, targetEntityId, targetEntityName, targetEntityType, null, null, null, null);
    }

    /**
     * Log an activity with related entity
     */
    @Transactional
    public void logActivity(ActivityLog.ActivityType type, String description, 
                           Long targetEntityId, String targetEntityName, ActivityLog.EntityType targetEntityType,
                           Long relatedEntityId, String relatedEntityName, ActivityLog.EntityType relatedEntityType,
                           String details) {
        
        String currentUsername = SecurityUtils.getCurrentUsername().orElse("system");
        User currentUser = userService.findByUsername(currentUsername);
        
        if (currentUser == null) {
            // If user not found, create a system activity log
            currentUser = new User();
            currentUser.setId(0L);
            currentUser.setFirstName("System");
            currentUser.setLastName("User");
            currentUser.setUsername("system");
        }

        ActivityLog activityLog = new ActivityLog();
        activityLog.setType(type);
        activityLog.setDescription(description);
        activityLog.setPerformedById(currentUser.getId());
        activityLog.setPerformedByName(currentUser.getFirstName() + " " + currentUser.getLastName());
        activityLog.setPerformedByUsername(currentUser.getUsername());
        activityLog.setTargetEntityId(targetEntityId);
        activityLog.setTargetEntityName(targetEntityName);
        activityLog.setTargetEntityType(targetEntityType);
        
        if (relatedEntityId != null) {
            activityLog.setRelatedEntityId(relatedEntityId);
            activityLog.setRelatedEntityName(relatedEntityName);
            activityLog.setRelatedEntityType(relatedEntityType);
        }
        
        if (details != null) {
            activityLog.setDetails(details);
        }

        activityLogRepository.save(activityLog);
    }

    /**
     * Get recent activities with pagination
     */
    public List<ActivityLog> getRecentActivities(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdDate"));
        return activityLogRepository.findAll(pageRequest).getContent();
    }

    /**
     * Log client creation
     */
    public void logClientCreated(Long clientId, String clientName) {
        logActivity(
            ActivityLog.ActivityType.CLIENT_CREATED,
            "Yeni müvekkil eklendi: " + clientName,
            clientId,
            clientName,
            ActivityLog.EntityType.CLIENT
        );
    }

    /**
     * Log case creation
     */
    public void logCaseCreated(Long caseId, String caseTitle, Long clientId, String clientName) {
        logActivity(
            ActivityLog.ActivityType.CASE_CREATED,
            "Yeni dava oluşturuldu: " + caseTitle,
            caseId,
            caseTitle,
            ActivityLog.EntityType.CASE,
            clientId,
            clientName,
            ActivityLog.EntityType.CLIENT,
            null
        );
    }

    /**
     * Log document creation
     */
    public void logDocumentCreated(Long documentId, String documentTitle, Long caseId, String caseTitle, Long clientId, String clientName) {
        String details = String.format("Dava: %s, Müvekkil: %s", caseTitle, clientName);
        
        logActivity(
            ActivityLog.ActivityType.DOCUMENT_CREATED,
            "Yeni doküman eklendi: " + documentTitle,
            documentId,
            documentTitle,
            ActivityLog.EntityType.DOCUMENT,
            clientId,
            clientName,
            ActivityLog.EntityType.CLIENT,
            details
        );
    }

    /**
     * Log user creation
     */
    public void logUserCreated(Long userId, String userName) {
        logActivity(
            ActivityLog.ActivityType.USER_CREATED,
            "Yeni kullanıcı oluşturuldu: " + userName,
            userId,
            userName,
            ActivityLog.EntityType.USER
        );
    }
} 