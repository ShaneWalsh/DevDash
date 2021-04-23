package dev.dash.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.dash.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
}
