package dev.dash.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dash.dao.DashboardConfigRepository;

@Service
public class BuilderService {

    @Autowired
    private DashboardConfigRepository dashboardConfigRepository;
 
    public void importSomething() {
        dashboardConfigRepository.findAll();
    }
}
