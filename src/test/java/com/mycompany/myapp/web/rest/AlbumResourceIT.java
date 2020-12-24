package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Album;
import com.mycompany.myapp.repository.AlbumRepository;

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
 * Integration tests for the {@link AlbumResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AlbumResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_RELEASE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RELEASE_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAlbumMockMvc;

    private Album album;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Album createEntity(EntityManager em) {
        Album album = new Album()
            .title(DEFAULT_TITLE)
            .releaseDate(DEFAULT_RELEASE_DATE);
        return album;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Album createUpdatedEntity(EntityManager em) {
        Album album = new Album()
            .title(UPDATED_TITLE)
            .releaseDate(UPDATED_RELEASE_DATE);
        return album;
    }

    @BeforeEach
    public void initTest() {
        album = createEntity(em);
    }

    @Test
    @Transactional
    void createAlbum() throws Exception {
        int databaseSizeBeforeCreate = albumRepository.findAll().size();
        // Create the Album
        restAlbumMockMvc.perform(post("/api/albums")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(album)))
            .andExpect(status().isCreated());

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll();
        assertThat(albumList).hasSize(databaseSizeBeforeCreate + 1);
        Album testAlbum = albumList.get(albumList.size() - 1);
        assertThat(testAlbum.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testAlbum.getReleaseDate()).isEqualTo(DEFAULT_RELEASE_DATE);
    }

    @Test
    @Transactional
    void createAlbumWithExistingId() throws Exception {
        // Create the Album with an existing ID
        album.setId(1L);

        int databaseSizeBeforeCreate = albumRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlbumMockMvc.perform(post("/api/albums")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(album)))
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll();
        assertThat(albumList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = albumRepository.findAll().size();
        // set the field null
        album.setTitle(null);

        // Create the Album, which fails.


        restAlbumMockMvc.perform(post("/api/albums")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(album)))
            .andExpect(status().isBadRequest());

        List<Album> albumList = albumRepository.findAll();
        assertThat(albumList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAlbums() throws Exception {
        // Initialize the database
        albumRepository.saveAndFlush(album);

        // Get all the albumList
        restAlbumMockMvc.perform(get("/api/albums?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(album.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(DEFAULT_RELEASE_DATE.toString())));
    }
    
    @Test
    @Transactional
    void getAlbum() throws Exception {
        // Initialize the database
        albumRepository.saveAndFlush(album);

        // Get the album
        restAlbumMockMvc.perform(get("/api/albums/{id}", album.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(album.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.releaseDate").value(DEFAULT_RELEASE_DATE.toString()));
    }
    @Test
    @Transactional
    void getNonExistingAlbum() throws Exception {
        // Get the album
        restAlbumMockMvc.perform(get("/api/albums/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateAlbum() throws Exception {
        // Initialize the database
        albumRepository.saveAndFlush(album);

        int databaseSizeBeforeUpdate = albumRepository.findAll().size();

        // Update the album
        Album updatedAlbum = albumRepository.findById(album.getId()).get();
        // Disconnect from session so that the updates on updatedAlbum are not directly saved in db
        em.detach(updatedAlbum);
        updatedAlbum
            .title(UPDATED_TITLE)
            .releaseDate(UPDATED_RELEASE_DATE);

        restAlbumMockMvc.perform(put("/api/albums")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedAlbum)))
            .andExpect(status().isOk());

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
        Album testAlbum = albumList.get(albumList.size() - 1);
        assertThat(testAlbum.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testAlbum.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    void updateNonExistingAlbum() throws Exception {
        int databaseSizeBeforeUpdate = albumRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlbumMockMvc.perform(put("/api/albums")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(album)))
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
    }

    
    @Test
    @Transactional
    void partialUpdateAlbumWithPatch() throws Exception {

// Initialize the database
albumRepository.saveAndFlush(album);

int databaseSizeBeforeUpdate = albumRepository.findAll().size();

// Update the album using partial update
Album partialUpdatedAlbum = new Album();
partialUpdatedAlbum.setId(album.getId());

    partialUpdatedAlbum
        .releaseDate(UPDATED_RELEASE_DATE);

restAlbumMockMvc.perform(patch("/api/albums")
.contentType("application/merge-patch+json")
.content(TestUtil.convertObjectToJsonBytes(partialUpdatedAlbum)))
.andExpect(status().isOk());

// Validate the Album in the database
List<Album> albumList = albumRepository.findAll();
assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
Album testAlbum = albumList.get(albumList.size() - 1);
    assertThat(testAlbum.getTitle()).isEqualTo(DEFAULT_TITLE);
    assertThat(testAlbum.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    void fullUpdateAlbumWithPatch() throws Exception {

// Initialize the database
albumRepository.saveAndFlush(album);

int databaseSizeBeforeUpdate = albumRepository.findAll().size();

// Update the album using partial update
Album partialUpdatedAlbum = new Album();
partialUpdatedAlbum.setId(album.getId());

    partialUpdatedAlbum
        .title(UPDATED_TITLE)
        .releaseDate(UPDATED_RELEASE_DATE);

restAlbumMockMvc.perform(patch("/api/albums")
.contentType("application/merge-patch+json")
.content(TestUtil.convertObjectToJsonBytes(partialUpdatedAlbum)))
.andExpect(status().isOk());

// Validate the Album in the database
List<Album> albumList = albumRepository.findAll();
assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
Album testAlbum = albumList.get(albumList.size() - 1);
    assertThat(testAlbum.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testAlbum.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    void partialUpdateAlbumShouldThrown() throws Exception {
        // Update the album without id should throw
        Album partialUpdatedAlbum = new Album();

        restAlbumMockMvc.perform(patch("/api/albums")
        .contentType("application/merge-patch+json")
        .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAlbum)))
        .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteAlbum() throws Exception {
        // Initialize the database
        albumRepository.saveAndFlush(album);

        int databaseSizeBeforeDelete = albumRepository.findAll().size();

        // Delete the album
        restAlbumMockMvc.perform(delete("/api/albums/{id}", album.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Album> albumList = albumRepository.findAll();
        assertThat(albumList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
