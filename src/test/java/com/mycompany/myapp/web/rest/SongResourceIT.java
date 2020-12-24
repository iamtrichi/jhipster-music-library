package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Song;
import com.mycompany.myapp.repository.SongRepository;

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
 * Integration tests for the {@link SongResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SongResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_RELEASE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RELEASE_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSongMockMvc;

    private Song song;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Song createEntity(EntityManager em) {
        Song song = new Song()
            .name(DEFAULT_NAME)
            .url(DEFAULT_URL)
            .releaseDate(DEFAULT_RELEASE_DATE);
        return song;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Song createUpdatedEntity(EntityManager em) {
        Song song = new Song()
            .name(UPDATED_NAME)
            .url(UPDATED_URL)
            .releaseDate(UPDATED_RELEASE_DATE);
        return song;
    }

    @BeforeEach
    public void initTest() {
        song = createEntity(em);
    }

    @Test
    @Transactional
    void createSong() throws Exception {
        int databaseSizeBeforeCreate = songRepository.findAll().size();
        // Create the Song
        restSongMockMvc.perform(post("/api/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isCreated());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeCreate + 1);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSong.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testSong.getReleaseDate()).isEqualTo(DEFAULT_RELEASE_DATE);
    }

    @Test
    @Transactional
    void createSongWithExistingId() throws Exception {
        // Create the Song with an existing ID
        song.setId(1L);

        int databaseSizeBeforeCreate = songRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSongMockMvc.perform(post("/api/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isBadRequest());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = songRepository.findAll().size();
        // set the field null
        song.setName(null);

        // Create the Song, which fails.


        restSongMockMvc.perform(post("/api/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isBadRequest());

        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = songRepository.findAll().size();
        // set the field null
        song.setUrl(null);

        // Create the Song, which fails.


        restSongMockMvc.perform(post("/api/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isBadRequest());

        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSongs() throws Exception {
        // Initialize the database
        songRepository.saveAndFlush(song);

        // Get all the songList
        restSongMockMvc.perform(get("/api/songs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(song.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(DEFAULT_RELEASE_DATE.toString())));
    }
    
    @Test
    @Transactional
    void getSong() throws Exception {
        // Initialize the database
        songRepository.saveAndFlush(song);

        // Get the song
        restSongMockMvc.perform(get("/api/songs/{id}", song.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(song.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.releaseDate").value(DEFAULT_RELEASE_DATE.toString()));
    }
    @Test
    @Transactional
    void getNonExistingSong() throws Exception {
        // Get the song
        restSongMockMvc.perform(get("/api/songs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateSong() throws Exception {
        // Initialize the database
        songRepository.saveAndFlush(song);

        int databaseSizeBeforeUpdate = songRepository.findAll().size();

        // Update the song
        Song updatedSong = songRepository.findById(song.getId()).get();
        // Disconnect from session so that the updates on updatedSong are not directly saved in db
        em.detach(updatedSong);
        updatedSong
            .name(UPDATED_NAME)
            .url(UPDATED_URL)
            .releaseDate(UPDATED_RELEASE_DATE);

        restSongMockMvc.perform(put("/api/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedSong)))
            .andExpect(status().isOk());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSong.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testSong.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    void updateNonExistingSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSongMockMvc.perform(put("/api/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(song)))
            .andExpect(status().isBadRequest());

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    
    @Test
    @Transactional
    void partialUpdateSongWithPatch() throws Exception {

// Initialize the database
songRepository.saveAndFlush(song);

int databaseSizeBeforeUpdate = songRepository.findAll().size();

// Update the song using partial update
Song partialUpdatedSong = new Song();
partialUpdatedSong.setId(song.getId());

    partialUpdatedSong
        .name(UPDATED_NAME)
        .url(UPDATED_URL)
        .releaseDate(UPDATED_RELEASE_DATE);

restSongMockMvc.perform(patch("/api/songs")
.contentType("application/merge-patch+json")
.content(TestUtil.convertObjectToJsonBytes(partialUpdatedSong)))
.andExpect(status().isOk());

// Validate the Song in the database
List<Song> songList = songRepository.findAll();
assertThat(songList).hasSize(databaseSizeBeforeUpdate);
Song testSong = songList.get(songList.size() - 1);
    assertThat(testSong.getName()).isEqualTo(UPDATED_NAME);
    assertThat(testSong.getUrl()).isEqualTo(UPDATED_URL);
    assertThat(testSong.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    void fullUpdateSongWithPatch() throws Exception {

// Initialize the database
songRepository.saveAndFlush(song);

int databaseSizeBeforeUpdate = songRepository.findAll().size();

// Update the song using partial update
Song partialUpdatedSong = new Song();
partialUpdatedSong.setId(song.getId());

    partialUpdatedSong
        .name(UPDATED_NAME)
        .url(UPDATED_URL)
        .releaseDate(UPDATED_RELEASE_DATE);

restSongMockMvc.perform(patch("/api/songs")
.contentType("application/merge-patch+json")
.content(TestUtil.convertObjectToJsonBytes(partialUpdatedSong)))
.andExpect(status().isOk());

// Validate the Song in the database
List<Song> songList = songRepository.findAll();
assertThat(songList).hasSize(databaseSizeBeforeUpdate);
Song testSong = songList.get(songList.size() - 1);
    assertThat(testSong.getName()).isEqualTo(UPDATED_NAME);
    assertThat(testSong.getUrl()).isEqualTo(UPDATED_URL);
    assertThat(testSong.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    void partialUpdateSongShouldThrown() throws Exception {
        // Update the song without id should throw
        Song partialUpdatedSong = new Song();

        restSongMockMvc.perform(patch("/api/songs")
        .contentType("application/merge-patch+json")
        .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSong)))
        .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteSong() throws Exception {
        // Initialize the database
        songRepository.saveAndFlush(song);

        int databaseSizeBeforeDelete = songRepository.findAll().size();

        // Delete the song
        restSongMockMvc.perform(delete("/api/songs/{id}", song.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Song> songList = songRepository.findAll();
        assertThat(songList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
