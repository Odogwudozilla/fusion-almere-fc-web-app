package com.fusionalmerefc.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;


public class UserDTO {
    @NotBlank(message = "External identifier cannot be blank.")
    private String externalIdentifier;

    @NotBlank(message = "Name cannot be blank.")
    @Size(max = 100, message = "Name cannot exceed 100 characters.")
    private String name;

    @NotBlank(message = "Username cannot be blank.")
    @Size(max = 50, message = "Username cannot exceed 50 characters.")
    private String username;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    private String email;

    @Size(max = 15, min = 10, message = "Mobile number is between 10 and 15 digits.")
    private String mobileNumber;

    @Size(max = 15, min = 10, message = "Whatsapp number is between 10 and 15 digits.")
    private String whatsappNumber;

    @Size(max = 20, message = "Postcode cannot exceed 20 characters.")
    private String postcode;

    @Size(max = 255, message = "Address cannot exceed 255 characters.")
    private String address;

    private String profilePictureUrl;

    @NotBlank(message = "Membership type cannot be blank.")
    private String membershipType;

    private LocalDateTime activatedAt;

    @NotBlank(message = "Status cannot be blank.")
    private String status;

    private List<RoleDTO> assignedRoles;

    // Getters and Setters
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

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getWhatsappNumber() {
        return this.whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getMembershipType() {
        return this.membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public LocalDateTime getActivatedAt() {
        return this.activatedAt;
    }

    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
   
    public List<RoleDTO> getAssignedRoles() {
        return this.assignedRoles;
    }

    public void setAssignedRoles(List<RoleDTO> assignedRoles) {
        this.assignedRoles = assignedRoles;
    }
}
