package dev.dash.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tabconfig")
public class TabConfig{
 
	@Id
	@Column(name="tabconfig_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
 
	@Column(name="code")
	private String code; 
 
	@Column(name="name")
    private String name;
    
    @Column(name="displayOrder")
	private Integer displayOrder;

    @OneToMany(mappedBy="tabConfig")
    private Set<PanelConfig> panelConfigSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboardconfig_id", nullable = false)
    private DashboardConfig dashboardConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "securityRole_id", nullable = true)
    private SecurityRole securityRole;

    protected TabConfig() {
    }

    public TabConfig(String code, String name, Integer displayOrder, DashboardConfig dashboardConfig) {
        this.code = code;
        this.name = name;
        this.displayOrder = displayOrder;
        this.dashboardConfig = dashboardConfig;
    }
    
}