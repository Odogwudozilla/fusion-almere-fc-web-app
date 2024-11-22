package com.fusionalmerefc.DTOs;

import java.util.List;

public class RoleDTO {
    private String externalIdentifier;
    private String name;
    private String description;
    private boolean isSuperUser;
    private List<PermissionDTO> assignedPermissions;

    public boolean getIsSuperUser() {
        return this.isSuperUser;
    }
    public void setIsSuperUser(boolean isSuperUser) {
        this.isSuperUser = isSuperUser;
    }

    public List<PermissionDTO> getAssignedPermissions() {
        return this.assignedPermissions;
    }

    public void setAssignedPermissions(List<PermissionDTO> assignedPermissions) {
        this.assignedPermissions = assignedPermissions;
    }

    public String getExternalIdentifier() {
        return this.externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
}
