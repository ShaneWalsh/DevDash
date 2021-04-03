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

import lombok.Data;

@Deprecated
@Data
@Entity
@Table(name = "fieldconfig")
public class FieldConfig{
 
	@Id
	@Column(name="fieldconfig_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
 
	@Column(name="code")
	private String code; 
 
	@Column(name="name")
    private String name;
    
    @Column(name="positionX")
    private Integer positionX;
    
    @Column(name="positionY")
    private Integer positionY;
    
    @Column(name="columnSizeX")
    private Integer columnSizeX;
    
    @Column(name="columnSizeY")
    private Integer columnSizeY;    

    /**
     * showIsNullable should allow this field to have checkboxs/select to allow a user to make this field
     * be used as a is null/not null check in a where clause
     */
    @Column(name="showIsNullable")
	private boolean showIsNullable;

    // todo investigate enums
    // select the datasource might be hardcoded data, e.g "value1","value2","value3","value4", or the result set of query via an identifier
    // boolean
    // text
    // number - rangeMin, rangeMax
    // date - rangeMin, rangeMax
	@Column(name="fieldType")
    private String fieldType;

    @Column(name="defaultValue")
    private String defaultValue;

    // advanced feature, may never be used
    @Column(name="pattern")
    private String pattern;

    /*
     This is an important field, it will be either an Identifier ${code} or an sql query
     If its an Identifier then this element will simply extract the values it needs from this identifier, else it will execute the query and use the result set.
     */
    @Column(name="dataSource")
	private String dataSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panelconfig_id", nullable = false)
    private PanelConfig panelConfig;

    // todo connection relationship

    protected FieldConfig() {
    }

    
}