package dev.dash.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.dash.model.PanelConfig;

@Repository
public interface PanelConfigRepository extends JpaRepository<PanelConfig, Long> {
    
}
