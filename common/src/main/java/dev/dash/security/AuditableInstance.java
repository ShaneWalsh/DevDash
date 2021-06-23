package dev.dash.security;

public class AuditableInstance implements Auditable {

    Long id;
    String classname;

    public AuditableInstance(Long id, String classname) {
        this.id = id;
        this.classname = classname;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getAuditableName(){
        return classname;
    }
    
}
