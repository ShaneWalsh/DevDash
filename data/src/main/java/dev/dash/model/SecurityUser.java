package dev.dash.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import dev.dash.security.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "securityUser")
public class SecurityUser implements Auditable{
 
	@Id
	@Column(name="securityUser_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
 
	@Column(name="username")
    private String username;
    
    @Column(name="password")
    private String password;

    @Column(name="userType")
    private String userType;

    @Column(name="disabledUser")
    private boolean disabledUser;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
    name = "securityUser_to_securityRole", 
    joinColumns = @JoinColumn(name = "securityUser_id"), 
    inverseJoinColumns = @JoinColumn(name = "securityRole_id"))
    private Set<SecurityRole> securityRolesSet;

    public SecurityUser(String username, String password, String userType) {
        this.setUsername(username);
        this.setPassword(password);
        this.setUserType(userType);
        this.disabledUser = false;
    }
    
    
}