package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Song;
import com.mycompany.myapp.repository.SongRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Song}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SongResource {

    private final Logger log = LoggerFactory.getLogger(SongResource.class);

    private static final String ENTITY_NAME = "song";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SongRepository songRepository;

    public SongResource(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    /**
     * {@code POST  /songs} : Create a new song.
     *
     * @param song the song to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new song, or with status {@code 400 (Bad Request)} if the song has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/songs")
    public ResponseEntity<Song> createSong(@Valid @RequestBody Song song) throws URISyntaxException {
        log.debug("REST request to save Song : {}", song);
        if (song.getId() != null) {
            throw new BadRequestAlertException("A new song cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Song result = songRepository.save(song);
        return ResponseEntity.created(new URI("/api/songs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /songs} : Updates an existing song.
     *
     * @param song the song to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated song,
     * or with status {@code 400 (Bad Request)} if the song is not valid,
     * or with status {@code 500 (Internal Server Error)} if the song couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/songs")
    public ResponseEntity<Song> updateSong(@Valid @RequestBody Song song) throws URISyntaxException {
        log.debug("REST request to update Song : {}", song);
        if (song.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Song result = songRepository.save(song);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, song.getId().toString()))
            .body(result);
    }


    /**
     * {@code PATCH  /songs} : Updates given fields of an existing song.
     *
     * @param song the song to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated song,
     * or with status {@code 400 (Bad Request)} if the song is not valid,
     * or with status {@code 404 (Not Found)} if the song is not found,
     * or with status {@code 500 (Internal Server Error)} if the song couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/songs", consumes = "application/merge-patch+json")
    public ResponseEntity<Song> partialUpdateSong(@NotNull @RequestBody Song song) throws URISyntaxException {
        log.debug("REST request to update Song partially : {}", song);
        if (song.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        
Optional<Song> result = songRepository.findById(song.getId())
   .map(existingSong -> {
   if (song.getName() != null) {
      existingSong.setName(song.getName());
   }

   if (song.getUrl() != null) {
      existingSong.setUrl(song.getUrl());
   }

   if (song.getReleaseDate() != null) {
      existingSong.setReleaseDate(song.getReleaseDate());
   }


   return existingSong;
   })
   .map(songRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, song.getId().toString())
        );
    }

    /**
     * {@code GET  /songs} : get all the songs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of songs in body.
     */
    @GetMapping("/songs")
    public ResponseEntity<List<Song>> getAllSongs(Pageable pageable) {
        log.debug("REST request to get a page of Songs");
        Page<Song> page = songRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /songs/:id} : get the "id" song.
     *
     * @param id the id of the song to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the song, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/songs/{id}")
    public ResponseEntity<Song> getSong(@PathVariable Long id) {
        log.debug("REST request to get Song : {}", id);
        Optional<Song> song = songRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(song);
    }

    /**
     * {@code DELETE  /songs/:id} : delete the "id" song.
     *
     * @param id the id of the song to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/songs/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        log.debug("REST request to delete Song : {}", id);
        songRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
