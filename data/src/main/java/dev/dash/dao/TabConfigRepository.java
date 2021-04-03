package dev.dash.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.dash.model.DashboardConfig;
import dev.dash.model.TabConfig;

@Repository
public interface TabConfigRepository extends JpaRepository<TabConfig, Long> {
    TabConfig findByCode(String code);

    Set<TabConfig> findByDashboardConfig(DashboardConfig dashboardConfig);
}