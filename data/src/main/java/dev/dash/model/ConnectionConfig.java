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

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "connectionconfig")
public class ConnectionConfig {
 
	@Id
	@Column(name="connectionconfig_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
 
	@Column(name="code", unique = true, length = 50)
    @Size(min = 1, max = 50)
	String code; 
 
	@Column(name="name")
    String name;

    /**
     * Mysql, sqlserver, REST etc
     */
    @Column(name="source")
    String source;
    
    // I dont think i need to the specify the driver because its in the url.
    @Column(name="driverType")
    String driverType;

    @Column(name="url")
    String url;

    @Column(name="username")
    String username;

    @Column(name="password")
    String password;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schemaconfig_id", nullable = false)
    private SchemaConfig schemaConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "securityRole_id", nullable = true)
    private SecurityRole securityRole;

    public ConnectionConfig(){}

    public ConnectionConfig(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public ConnectionConfig(String code, String name, String source, String url, String username, String password,
            SchemaConfig schemaConfig) {
        this.code = code;
        this.name = name;
        this.source = source;
        this.url = url;
        this.username = username;
        this.password = password;
        this.schemaConfig = schemaConfig;
    }

    
}