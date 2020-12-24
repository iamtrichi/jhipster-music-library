package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Artiste;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Artiste}.
 */
public interface ArtisteService {

    /**
     * Save a artiste.
     *
     * @param artiste the entity to save.
     * @return the persisted entity.
     */
    Artiste save(Artiste artiste);

    /**
     * Partially updates a artiste.
     *
     * @param artiste the entity to update partially.
     * @return the persisted entity.
     */
Optional<Artiste> partialUpdate(Artiste artiste);

    /**
     * Get all the artistes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Artiste> findAll(Pageable pageable);


    /**
     * Get the "id" artiste.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Artiste> findOne(Long id);

    /**
     * Delete the "id" artiste.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
