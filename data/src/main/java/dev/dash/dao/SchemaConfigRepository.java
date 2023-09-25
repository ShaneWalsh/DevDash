package dev.dash.dao;

import org.springframework.stereotype.Repository;

import dev.dash.model.SchemaConfig;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SchemaConfigRepository extends JpaRepository<SchemaConfig, Long> {
    SchemaConfig findByCode(String code);
    boolean existsByCode(String code);
    List<SchemaConfig> findByDashboardConfigsSet_Code(String dashboardCode);
}