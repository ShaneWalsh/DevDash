package dev.dash.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dash.dao.DashboardConfigRepository;
import dev.dash.model.DashboardConfig;

public interface DashboardConfigService {
 
    public List<DashboardConfig> list();
}