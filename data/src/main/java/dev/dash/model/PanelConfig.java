package dev.dash.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "panelconfig")
public class PanelConfig{
 
    @JsonIgnore
	@Id
	@Column(name="panelconfig_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
 
	@Column(name="code")
	private String code; 
 
	@Column(name="name")
    private String name;
    
    @Column(name="gridRow")
    private Integer gridRow;
    
    @Column(name="gridCol")
    private Integer gridCol;  
    
    @Column(name="showRefresh")
	private boolean showRefresh;

	@Column(name="elements", columnDefinition = "TEXT") // BLOB or TEXT or LOB or length=1000?
    private String elements;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tabconfig_id", nullable = false)
    private TabConfig tabConfig;

    protected PanelConfig() {
    }

    public PanelConfig(String code, String name, String elements, TabConfig tabConfig) {
        this.code = code;
        this.name = name;
        this.elements = elements;
        this.tabConfig = tabConfig;
    }

    public PanelConfig(String code, String name, Integer col, Integer row, String elements, TabConfig tabConfig) {
        this.code = code;
        this.name = name;
        this.gridCol = col;
        this.gridRow = row;
        this.elements = elements;
        this.tabConfig = tabConfig;
    }
}