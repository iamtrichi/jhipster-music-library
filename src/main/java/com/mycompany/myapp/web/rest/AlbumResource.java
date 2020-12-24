package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Album;
import com.mycompany.myapp.service.AlbumService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;

import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Album}.
 */
@RestController
@RequestMapping("/api")
public class AlbumResource {

    private final Logger log = LoggerFactory.getLogger(AlbumResource.class);

    private static final String ENTITY_NAME = "album";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AlbumService albumService;

    public AlbumResource(AlbumService albumService) {
        this.albumService = albumService;
    }

    /**
     * {@code POST  /albums} : Create a new album.
     *
     * @param album the album to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new album, or with status {@code 400 (Bad Request)} if the album has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/albums")
    public ResponseEntity<Album> createAlbum(@Valid @RequestBody Album album) throws URISyntaxException {
        log.debug("REST request to save Album : {}", album);
        if (album.getId() != null) {
            throw new BadRequestAlertException("A new album cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Album result = albumService.save(album);
        return ResponseEntity.created(new URI("/api/albums/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /albums} : Updates an existing album.
     *
     * @param album the album to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated album,
     * or with status {@code 400 (Bad Request)} if the album is not valid,
     * or with status {@code 500 (Internal Server Error)} if the album couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/albums")
    public ResponseEntity<Album> updateAlbum(@Valid @RequestBody Album album) throws URISyntaxException {
        log.debug("REST request to update Album : {}", album);
        if (album.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Album result = albumService.save(album);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, album.getId().toString()))
            .body(result);
    }


    /**
     * {@code PATCH  /albums} : Updates given fields of an existing album.
     *
     * @param album the album to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated album,
     * or with status {@code 400 (Bad Request)} if the album is not valid,
     * or with status {@code 404 (Not Found)} if the album is not found,
     * or with status {@code 500 (Internal Server Error)} if the album couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/albums", consumes = "application/merge-patch+json")
    public ResponseEntity<Album> partialUpdateAlbum(@NotNull @RequestBody Album album) throws URISyntaxException {
        log.debug("REST request to update Album partially : {}", album);
        if (album.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        

   Optional<Album> result = albumService.partialUpdate(album);


        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, album.getId().toString())
        );
    }

    /**
     * {@code GET  /albums} : get all the albums.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of albums in body.
     */
    @GetMapping("/albums")
    public ResponseEntity<List<Album>> getAllAlbums(Pageable pageable) {
        log.debug("REST request to get a page of Albums");
        Page<Album> page = albumService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /albums/:id} : get the "id" album.
     *
     * @param id the id of the album to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the album, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/albums/{id}")
    public ResponseEntity<Album> getAlbum(@PathVariable Long id) {
        log.debug("REST request to get Album : {}", id);
        Optional<Album> album = albumService.findOne(id);
        return ResponseUtil.wrapOrNotFound(album);
    }

    /**
     * {@code DELETE  /albums/:id} : delete the "id" album.
     *
     * @param id the id of the album to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/albums/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        log.debug("REST request to delete Album : {}", id);
        albumService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
