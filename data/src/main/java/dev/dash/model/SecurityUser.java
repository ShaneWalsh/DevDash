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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "securityUser")
public class SecurityUser {
 
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

    public SecurityUser(String username, String password, String userType) {
        this.setUsername(username);
        this.setPassword(password);
        this.setUserType(userType);
    }
    
    
}