package com.fusionalmerefc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    /**
     * Find a record by its external identifier.
     * 
     * @param externalIdentifier the external identifier
     * @return an Optional containing the entity, if found
     */
    Optional<T> findByExternalIdentifier(String externalIdentifier);

    /**
     * Check if a record exists by its external identifier.
     * 
     * @param externalIdentifier the external identifier
     * @return true if the entity exists, false otherwise
     */
    boolean existsByExternalIdentifier(String externalIdentifier);
}

