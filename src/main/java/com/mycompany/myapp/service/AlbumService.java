package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Album;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Album}.
 */
public interface AlbumService {

    /**
     * Save a album.
     *
     * @param album the entity to save.
     * @return the persisted entity.
     */
    Album save(Album album);

    /**
     * Partially updates a album.
     *
     * @param album the entity to update partially.
     * @return the persisted entity.
     */
Optional<Album> partialUpdate(Album album);

    /**
     * Get all the albums.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Album> findAll(Pageable pageable);


    /**
     * Get the "id" album.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Album> findOne(Long id);

    /**
     * Delete the "id" album.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
