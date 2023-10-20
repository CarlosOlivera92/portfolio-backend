package com.charlesxvr.portfoliobackend.security.enums;

import java.util.Arrays;
import java.util.List;

public enum Role {
    CUSTOMER(Arrays.asList(Permission.READ_ALL_USERS)),
    ADMINISTRATOR(Arrays.asList(Permission.READ_ALL_USERS, Permission.DELETE_ONE_USER));
    private List<Permission> permissions;
    public List<Permission> getPermissions() { return permissions; }
    public void setPermissions(List<Permission> permissions) { this.permissions = permissions; }
    Role(List<Permission> permissions) {
        this.permissions = permissions;
    }

}
