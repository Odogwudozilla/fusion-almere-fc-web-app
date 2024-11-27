package com.fusionalmerefc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_socials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSocials extends BaseEntity{

    @Column(name = "twitter_handle", length = 255)
    private String twitterHandle;
    
    @Column(name = "instagram_username", length = 255)
    private String instagramUsername;

    @Column(name = "facebook_profile_url", length = 500)
    private String facebookProfileUrl;

    @Column(name = "tiktok_profile_url", length = 500)
    private String tiktokProfileUrl;

    @Column(name = "linkedin_profile_url", length = 500)
    private String linkedinProfileUrl;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
