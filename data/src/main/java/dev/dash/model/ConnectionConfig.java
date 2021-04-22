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

import lombok.Data;
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

    // todo work out we I want to support different connection types
    // read /read write
    @Column(name="connectionType")
    String connectionType;

    /**
     * Mysql, sqlserver etc
     */
    @Column(name="language")
    String language;

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

    public ConnectionConfig(String code, String name, String language, String url, String userame, String password,
            SchemaConfig schemaConfig) {
        this.code = code;
        this.name = name;
        this.language = language;
        this.url = url;
        this.username = userame;
        this.password = password;
        this.schemaConfig = schemaConfig;
    }

    
}