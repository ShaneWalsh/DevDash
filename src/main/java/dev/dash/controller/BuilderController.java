package dev.dash.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.dash.dao.ConnectionConfigRepository;
import dev.dash.dao.QueryConfigRepository;
import dev.dash.dao.SchemaConfigRepository;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.SchemaConfig;

@RestController
@RequestMapping("builder")
public class BuilderController {

    @Autowired
    SchemaConfigRepository schemaConfigRepository;

    @Autowired
    QueryConfigRepository queryConfigRepository;

    @Autowired
    ConnectionConfigRepository connectionConfigRepository;

    @GetMapping("")
	public String index() {
		return "hello world";
    }

	@GetMapping("/list")
	public List<SchemaConfig> list() {
        List<SchemaConfig> schemaConfigs = schemaConfigRepository.findAll();
        List<QueryConfig> qConfigs = queryConfigRepository.findAll();
        List<ConnectionConfig> connectionConfigs = connectionConfigRepository.findAll();

		return schemaConfigs;
    }

    // crud operations for the different objects.
        // or an individual save and determine whats diferent?
}

class BuilderData {
    // should have the schema's, connections, and queries.
}
