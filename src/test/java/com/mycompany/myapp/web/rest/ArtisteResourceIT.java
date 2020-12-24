package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Artiste;
import com.mycompany.myapp.repository.ArtisteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ArtisteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ArtisteResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_NAISS = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_NAISS = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private ArtisteRepository artisteRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restArtisteMockMvc;

    private Artiste artiste;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artiste createEntity(EntityManager em) {
        Artiste artiste = new Artiste()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .dateNaiss(DEFAULT_DATE_NAISS);
        return artiste;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artiste createUpdatedEntity(EntityManager em) {
        Artiste artiste = new Artiste()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .dateNaiss(UPDATED_DATE_NAISS);
        return artiste;
    }

    @BeforeEach
    public void initTest() {
        artiste = createEntity(em);
    }

    @Test
    @Transactional
    void createArtiste() throws Exception {
        int databaseSizeBeforeCreate = artisteRepository.findAll().size();
        // Create the Artiste
        restArtisteMockMvc.perform(post("/api/artistes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(artiste)))
            .andExpect(status().isCreated());

        // Validate the Artiste in the database
        List<Artiste> artisteList = artisteRepository.findAll();
        assertThat(artisteList).hasSize(databaseSizeBeforeCreate + 1);
        Artiste testArtiste = artisteList.get(artisteList.size() - 1);
        assertThat(testArtiste.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testArtiste.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testArtiste.getDateNaiss()).isEqualTo(DEFAULT_DATE_NAISS);
    }

    @Test
    @Transactional
    void createArtisteWithExistingId() throws Exception {
        // Create the Artiste with an existing ID
        artiste.setId(1L);

        int databaseSizeBeforeCreate = artisteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restArtisteMockMvc.perform(post("/api/artistes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(artiste)))
            .andExpect(status().isBadRequest());

        // Validate the Artiste in the database
        List<Artiste> artisteList = artisteRepository.findAll();
        assertThat(artisteList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = artisteRepository.findAll().size();
        // set the field null
        artiste.setFirstName(null);

        // Create the Artiste, which fails.


        restArtisteMockMvc.perform(post("/api/artistes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(artiste)))
            .andExpect(status().isBadRequest());

        List<Artiste> artisteList = artisteRepository.findAll();
        assertThat(artisteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = artisteRepository.findAll().size();
        // set the field null
        artiste.setLastName(null);

        // Create the Artiste, which fails.


        restArtisteMockMvc.perform(post("/api/artistes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(artiste)))
            .andExpect(status().isBadRequest());

        List<Artiste> artisteList = artisteRepository.findAll();
        assertThat(artisteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateNaissIsRequired() throws Exception {
        int databaseSizeBeforeTest = artisteRepository.findAll().size();
        // set the field null
        artiste.setDateNaiss(null);

        // Create the Artiste, which fails.


        restArtisteMockMvc.perform(post("/api/artistes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(artiste)))
            .andExpect(status().isBadRequest());

        List<Artiste> artisteList = artisteRepository.findAll();
        assertThat(artisteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllArtistes() throws Exception {
        // Initialize the database
        artisteRepository.saveAndFlush(artiste);

        // Get all the artisteList
        restArtisteMockMvc.perform(get("/api/artistes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(artiste.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].dateNaiss").value(hasItem(DEFAULT_DATE_NAISS.toString())));
    }
    
    @Test
    @Transactional
    void getArtiste() throws Exception {
        // Initialize the database
        artisteRepository.saveAndFlush(artiste);

        // Get the artiste
        restArtisteMockMvc.perform(get("/api/artistes/{id}", artiste.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(artiste.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.dateNaiss").value(DEFAULT_DATE_NAISS.toString()));
    }
    @Test
    @Transactional
    void getNonExistingArtiste() throws Exception {
        // Get the artiste
        restArtisteMockMvc.perform(get("/api/artistes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateArtiste() throws Exception {
        // Initialize the database
        artisteRepository.saveAndFlush(artiste);

        int databaseSizeBeforeUpdate = artisteRepository.findAll().size();

        // Update the artiste
        Artiste updatedArtiste = artisteRepository.findById(artiste.getId()).get();
        // Disconnect from session so that the updates on updatedArtiste are not directly saved in db
        em.detach(updatedArtiste);
        updatedArtiste
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .dateNaiss(UPDATED_DATE_NAISS);

        restArtisteMockMvc.perform(put("/api/artistes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedArtiste)))
            .andExpect(status().isOk());

        // Validate the Artiste in the database
        List<Artiste> artisteList = artisteRepository.findAll();
        assertThat(artisteList).hasSize(databaseSizeBeforeUpdate);
        Artiste testArtiste = artisteList.get(artisteList.size() - 1);
        assertThat(testArtiste.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testArtiste.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testArtiste.getDateNaiss()).isEqualTo(UPDATED_DATE_NAISS);
    }

    @Test
    @Transactional
    void updateNonExistingArtiste() throws Exception {
        int databaseSizeBeforeUpdate = artisteRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArtisteMockMvc.perform(put("/api/artistes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(artiste)))
            .andExpect(status().isBadRequest());

        // Validate the Artiste in the database
        List<Artiste> artisteList = artisteRepository.findAll();
        assertThat(artisteList).hasSize(databaseSizeBeforeUpdate);
    }

    
    @Test
    @Transactional
    void partialUpdateArtisteWithPatch() throws Exception {

// Initialize the database
artisteRepository.saveAndFlush(artiste);

int databaseSizeBeforeUpdate = artisteRepository.findAll().size();

// Update the artiste using partial update
Artiste partialUpdatedArtiste = new Artiste();
partialUpdatedArtiste.setId(artiste.getId());

    partialUpdatedArtiste
        .firstName(UPDATED_FIRST_NAME)
        .lastName(UPDATED_LAST_NAME);

restArtisteMockMvc.perform(patch("/api/artistes")
.contentType("application/merge-patch+json")
.content(TestUtil.convertObjectToJsonBytes(partialUpdatedArtiste)))
.andExpect(status().isOk());

// Validate the Artiste in the database
List<Artiste> artisteList = artisteRepository.findAll();
assertThat(artisteList).hasSize(databaseSizeBeforeUpdate);
Artiste testArtiste = artisteList.get(artisteList.size() - 1);
    assertThat(testArtiste.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
    assertThat(testArtiste.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    assertThat(testArtiste.getDateNaiss()).isEqualTo(DEFAULT_DATE_NAISS);
    }

    @Test
    @Transactional
    void fullUpdateArtisteWithPatch() throws Exception {

// Initialize the database
artisteRepository.saveAndFlush(artiste);

int databaseSizeBeforeUpdate = artisteRepository.findAll().size();

// Update the artiste using partial update
Artiste partialUpdatedArtiste = new Artiste();
partialUpdatedArtiste.setId(artiste.getId());

    partialUpdatedArtiste
        .firstName(UPDATED_FIRST_NAME)
        .lastName(UPDATED_LAST_NAME)
        .dateNaiss(UPDATED_DATE_NAISS);

restArtisteMockMvc.perform(patch("/api/artistes")
.contentType("application/merge-patch+json")
.content(TestUtil.convertObjectToJsonBytes(partialUpdatedArtiste)))
.andExpect(status().isOk());

// Validate the Artiste in the database
List<Artiste> artisteList = artisteRepository.findAll();
assertThat(artisteList).hasSize(databaseSizeBeforeUpdate);
Artiste testArtiste = artisteList.get(artisteList.size() - 1);
    assertThat(testArtiste.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
    assertThat(testArtiste.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    assertThat(testArtiste.getDateNaiss()).isEqualTo(UPDATED_DATE_NAISS);
    }

    @Test
    @Transactional
    void partialUpdateArtisteShouldThrown() throws Exception {
        // Update the artiste without id should throw
        Artiste partialUpdatedArtiste = new Artiste();

        restArtisteMockMvc.perform(patch("/api/artistes")
        .contentType("application/merge-patch+json")
        .content(TestUtil.convertObjectToJsonBytes(partialUpdatedArtiste)))
        .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteArtiste() throws Exception {
        // Initialize the database
        artisteRepository.saveAndFlush(artiste);

        int databaseSizeBeforeDelete = artisteRepository.findAll().size();

        // Delete the artiste
        restArtisteMockMvc.perform(delete("/api/artistes/{id}", artiste.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Artiste> artisteList = artisteRepository.findAll();
        assertThat(artisteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
