package com.fusionalmerefc.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "roles", uniqueConstraints = {
    @UniqueConstraint(columnNames = "external_identifier")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;
    
    @Column(name = "external_identifier")
    private String externalIdentifier;
}
