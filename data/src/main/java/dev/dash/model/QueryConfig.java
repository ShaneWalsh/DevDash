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
import javax.validation.constraints.Size;

import dev.dash.enums.DdlTypeEnum;
import dev.dash.security.Auditable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "queryconfig")
public class QueryConfig implements Auditable {
	@Id
	@Column(name="queryConfig_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
 
    @Column(name="code", unique = true, length = 50)
    @Size(min = 1, max = 50)
	private String code; 
 
	@Column(name="description")
    private String description;

    /**
     * The Method of the SQL query or HTTP verb
     */
    @Column(name="ddl_type")
    private String ddlType;

    /**
     * URL path for HTTP methods, appended to Connection URL
     */
    @Column(name="path")
    private String path;

    /**
     * The body of the SQL query or HTTP request
     */
    @Column(name="queryString", length = 15000)
    private String queryString;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schemaconfig_id", nullable = false)
    private SchemaConfig schemaConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "securityRole_id", nullable = true)
    private SecurityRole securityRole;

    public QueryConfig(){}

    public QueryConfig(String code, String description, String queryString) {
        this.code = code;
        this.description = description;
        this.queryString = queryString;
        this.ddlType = DdlTypeEnum.Select.name();
    }

    public QueryConfig(String code, String description, String queryString, SchemaConfig schemaConfig) {
        this.code = code;
        this.description = description;
        this.queryString = queryString;
        this.schemaConfig = schemaConfig;
        this.ddlType = DdlTypeEnum.Select.name();
    }

    public QueryConfig(String code, String description, String queryString, String ddlType, SchemaConfig schemaConfig) {
        this.code = code;
        this.description = description;
        this.queryString = queryString;
        this.ddlType = ddlType;
        this.schemaConfig = schemaConfig;
    }
}