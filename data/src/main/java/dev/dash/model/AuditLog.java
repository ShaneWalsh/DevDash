package dev.dash.model;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auditLog")
public class AuditLog {

    @Id
	@Column(name="auditLog_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="corId")
    @Type(type="uuid-char")
    private UUID corId;

    @Column(name="eventType")
    private String eventType;

    @Column(name="eventTime")
    private Timestamp eventTime;
    
    @Column(name="securityUser")
    private String securityUser;

    @Column(name="objectType")
    private String objectType;

    @Column(name="objectId")
    private Long objectId;

    @Column(name="dataJson", columnDefinition = "TEXT")
    private String dataJson;

    public AuditLog(UUID corId, String eventType, Timestamp eventTime, String securityUser, String objectType, Long objectId, String dataJson) {
        this.corId = corId;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.securityUser = securityUser;
        this.objectType = objectType;
        this.objectId = objectId;
        this.dataJson = dataJson;
    }

}
