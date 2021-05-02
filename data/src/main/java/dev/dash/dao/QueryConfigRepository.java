package dev.dash.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.dash.model.QueryConfig;

@Repository
public interface QueryConfigRepository extends JpaRepository<QueryConfig, Long> {
    
    QueryConfig findByCode(String code);
    boolean existsByCode(String code);
}