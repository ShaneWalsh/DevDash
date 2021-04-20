package dev.dash.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "securityRole")
public class SecurityRole {
 
	@Id
	@Column(name="securityRole_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
 
	@Column(name="code")
    private String code;
    
    @Column(name="description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional=true)
    @JoinColumn(name = "parent_securityRole_id")
    private SecurityRole parentSecurityRole;

    @OneToMany(mappedBy="parentSecurityRole", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=false)
    private Set<SecurityRole> children = new HashSet<SecurityRole>();

    public SecurityRole(String code, String description) {
        this.setCode(code);
        this.setDescription(description);
    }    
    
    public SecurityRole(String code, String description, SecurityRole parentSecurityRole) {
        this.setCode(code);
        this.setDescription(description);
        this.setParentSecurityRole(parentSecurityRole);
    }
    
}