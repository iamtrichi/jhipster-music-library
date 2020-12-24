package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Artiste;
import com.mycompany.myapp.service.ArtisteService;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Artiste}.
 */
@RestController
@RequestMapping("/api")
public class ArtisteResource {

    private final Logger log = LoggerFactory.getLogger(ArtisteResource.class);

    private static final String ENTITY_NAME = "artiste";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ArtisteService artisteService;

    public ArtisteResource(ArtisteService artisteService) {
        this.artisteService = artisteService;
    }

    /**
     * {@code POST  /artistes} : Create a new artiste.
     *
     * @param artiste the artiste to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new artiste, or with status {@code 400 (Bad Request)} if the artiste has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/artistes")
    public ResponseEntity<Artiste> createArtiste(@Valid @RequestBody Artiste artiste) throws URISyntaxException {
        log.debug("REST request to save Artiste : {}", artiste);
        if (artiste.getId() != null) {
            throw new BadRequestAlertException("A new artiste cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Artiste result = artisteService.save(artiste);
        return ResponseEntity.created(new URI("/api/artistes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /artistes} : Updates an existing artiste.
     *
     * @param artiste the artiste to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated artiste,
     * or with status {@code 400 (Bad Request)} if the artiste is not valid,
     * or with status {@code 500 (Internal Server Error)} if the artiste couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/artistes")
    public ResponseEntity<Artiste> updateArtiste(@Valid @RequestBody Artiste artiste) throws URISyntaxException {
        log.debug("REST request to update Artiste : {}", artiste);
        if (artiste.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Artiste result = artisteService.save(artiste);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, artiste.getId().toString()))
            .body(result);
    }


    /**
     * {@code PATCH  /artistes} : Updates given fields of an existing artiste.
     *
     * @param artiste the artiste to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated artiste,
     * or with status {@code 400 (Bad Request)} if the artiste is not valid,
     * or with status {@code 404 (Not Found)} if the artiste is not found,
     * or with status {@code 500 (Internal Server Error)} if the artiste couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/artistes", consumes = "application/merge-patch+json")
    public ResponseEntity<Artiste> partialUpdateArtiste(@NotNull @RequestBody Artiste artiste) throws URISyntaxException {
        log.debug("REST request to update Artiste partially : {}", artiste);
        if (artiste.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        

   Optional<Artiste> result = artisteService.partialUpdate(artiste);


        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, artiste.getId().toString())
        );
    }

    /**
     * {@code GET  /artistes} : get all the artistes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of artistes in body.
     */
    @GetMapping("/artistes")
    public ResponseEntity<List<Artiste>> getAllArtistes(Pageable pageable) {
        log.debug("REST request to get a page of Artistes");
        Page<Artiste> page = artisteService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /artistes/:id} : get the "id" artiste.
     *
     * @param id the id of the artiste to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the artiste, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/artistes/{id}")
    public ResponseEntity<Artiste> getArtiste(@PathVariable Long id) {
        log.debug("REST request to get Artiste : {}", id);
        Optional<Artiste> artiste = artisteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(artiste);
    }

    /**
     * {@code DELETE  /artistes/:id} : delete the "id" artiste.
     *
     * @param id the id of the artiste to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/artistes/{id}")
    public ResponseEntity<Void> deleteArtiste(@PathVariable Long id) {
        log.debug("REST request to delete Artiste : {}", id);
        artisteService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
