package dev.dash.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.dash.model.ConnectionConfig;

@Repository
public interface ConnectionConfigRepository extends JpaRepository<ConnectionConfig, Long> {

    ConnectionConfig findByCode(String code);

}