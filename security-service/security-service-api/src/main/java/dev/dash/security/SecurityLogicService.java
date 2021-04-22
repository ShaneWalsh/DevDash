package dev.dash.security;

import dev.dash.model.SecurityRole;

public interface SecurityLogicService {

    /**
     * Checks if the currently logged in user has the required permission.
     * @return true if the logged in user has the required role or the role is null.
     */
    public boolean checkUserHasRole(SecurityRole requiredRole);

    /**
     * Checks if the currently logged in user has the required permission.
     * @return true if the logged in user has the required role.
     */
    public boolean checkUserHasRole(String requiredRole);

}
