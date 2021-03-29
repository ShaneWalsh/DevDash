package dev.dash.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class SchemaConfig {
    
    @Id
	@Column(name="schemaconfig_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
 
	@Column(name="code", unique=true, length = 100)
	private String code; 
 
	@Column(name="name")
    private String name;

    @OneToMany(mappedBy="schemaConfig")
    private Set<ConnectionConfig> connectionConfigSet;

    @OneToMany(mappedBy="schemaConfig")
    private Set<QueryConfig> queryConfigsSet;

    @ManyToMany(mappedBy="schemaConfigSet")
    private Set<DashboardConfig> dashboardConfigsSet;

    public SchemaConfig(){}

    public SchemaConfig(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    
}