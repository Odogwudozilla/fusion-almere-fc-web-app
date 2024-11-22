package com.fusionalmerefc.model;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "permissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = "external_identifier")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Permission extends BaseEntity{

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;
    
    @Column(name = "is_for_super_user_only", nullable = false)
    @JsonProperty("isForSuperUserOnly") // Explicit mapping
    private boolean isForSuperUserOnly;

}
