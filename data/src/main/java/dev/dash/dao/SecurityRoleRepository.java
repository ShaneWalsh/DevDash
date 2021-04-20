package dev.dash.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.dash.model.SecurityRole;

@Repository
public interface SecurityRoleRepository extends JpaRepository<SecurityRole, Long> {
    SecurityRole findByCode(String code);
}
