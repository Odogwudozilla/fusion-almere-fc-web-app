package com.fusionalmerefc.DTOs;


public class PermissionDTO {
    private String externalIdentifier;
    private String name;
    private String description;
    private boolean isForSuperUserOnly;

    public void setIsForSuperUserOnly(boolean isForSuperUserOnly) {
        this.isForSuperUserOnly = isForSuperUserOnly;
    }

    public PermissionDTO(String externalIdentifier, String name, String description, boolean isForSuperUserOnly) {
        this.externalIdentifier = externalIdentifier;
        this.name = name;
        this.description = description;
        this.isForSuperUserOnly = isForSuperUserOnly;
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

    public boolean isIsForSuperUserOnly() {
        return this.isForSuperUserOnly;
    }

    public boolean getIsForSuperUserOnly() {
        return this.isForSuperUserOnly;
    }
}
