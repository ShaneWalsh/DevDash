package dev.dash.dao;

import org.springframework.stereotype.Repository;

import dev.dash.model.SchemaConfig;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SchemaConfigRepository extends JpaRepository<SchemaConfig, Long> {

    SchemaConfig findByCode(String code);
}