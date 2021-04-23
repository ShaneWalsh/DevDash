package dev.dash.security;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import dev.dash.dao.AuditLogRepository;
import dev.dash.enums.AuditEventTypeEnum;
import dev.dash.model.AuditLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuditLogicServiceImpl  implements AuditLogicService {
    
    @Autowired
    AuditLogRepository auditLogRepository;

    @Override
    public void auditEntityEvent(Auditable auditable, AuditEventTypeEnum auditEntityChange, String jsonData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication != null && authentication.isAuthenticated() ) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            insertAudit( auditEntityChange.name(), userDetails.getUsername(), jsonData, auditable.getAuditableName(), auditable.getId() );
        } else {
            log.error("Unauthenticated entity change {} {}", auditable.getAuditableName(), auditable.getId());
            insertAudit( auditEntityChange.name(), "Unauthenticated", jsonData, auditable.getAuditableName(), auditable.getId() );
        }
    }

    private void insertAudit(String eventType, String securityUser, String dataJson, String objectType, Long objectId){
        // todo get the UUID from the session, x-correlation-id in the header, add filter to extract it and put it in the MDC
        UUID corId = UUID.randomUUID();

        AuditLog auditLog = new AuditLog( corId, eventType, new Timestamp((new Date()).getTime()), 
            securityUser, objectType, objectId, dataJson);
        auditLogRepository.saveAndFlush( auditLog );
    }

}
