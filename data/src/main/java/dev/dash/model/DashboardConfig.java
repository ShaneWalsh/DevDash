package dev.dash.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dashboardconfig")
public class DashboardConfig {
 
	@Id
	@Column(name="dashboardconfig_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
 
	@Column(name="code")
	String code; 
 
	@Column(name="name")
    String name;

    @OneToMany(mappedBy="dashboardConfig")
    private Set<TabConfig> tabConfigSet;

    @ManyToMany
    @JoinTable(
    name = "dashboardconfig_to_schemaconfig", 
    joinColumns = @JoinColumn(name = "dashboardconfig_id"), 
    inverseJoinColumns = @JoinColumn(name = "schemaconfig_id"))
    private Set<SchemaConfig> schemaConfigSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "securityRole_id", nullable = true)
    private SecurityRole securityRole;
    
    public DashboardConfig() {
    }

    public DashboardConfig(String code, String name) {
        this.code = code;
        this.name = name;
    }
}