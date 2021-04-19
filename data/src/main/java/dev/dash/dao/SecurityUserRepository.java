package dev.dash.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.dash.model.SecurityUser;

@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {
    SecurityUser findByUsername(String username);
}
