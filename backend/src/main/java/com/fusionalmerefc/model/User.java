package com.fusionalmerefc.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fusionalmerefc.model.constants.MembershipType;
import com.fusionalmerefc.model.constants.StatusType;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private String password;
    
    private String address;
    
    private String postcode;

    @Column(name = "whatsapp_number", nullable = false)
    private String whatsappNumber;

    @Column(name = "mobile_number")
    private String mobileNumber;
    
    @Column(name = "identifier_for_url")
    private String identifierForUrl;
    
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;
    
    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusType status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType membershipType;

}

