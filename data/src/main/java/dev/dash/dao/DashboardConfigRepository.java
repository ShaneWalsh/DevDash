package dev.dash.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.dash.model.DashboardConfig;

@Repository
public interface DashboardConfigRepository extends JpaRepository<DashboardConfig, Long> {
    DashboardConfig findByCode(String code);
    boolean existsByCode(String code);
}