package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A Album.
 */
@Entity
@Table(name = "album")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @OneToMany(mappedBy = "album")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = {
        "album",
    }, allowSetters = true)
    private Set<Song> songs = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = {
        "makes",
    }, allowSetters = true)
    private Artiste artiste;


    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Album id(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Album title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return this.releaseDate;
    }

    public Album releaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Set<Song> getSongs() {
        return this.songs;
    }

    public Album songs(Set<Song> songs) {
        this.setSongs(songs);
        return this;
    }

    public Album addSongs(Song song) {
        this.songs.add(song);
        song.setAlbum(this);
        return this;
    }

    public Album removeSongs(Song song) {
        this.songs.remove(song);
        song.setAlbum(null);
        return this;
    }

    public void setSongs(Set<Song> songs) {
        if (this.songs != null) {
            this.songs.forEach(i -> i.setAlbum(null));
        }
        if (songs != null) {
            songs.forEach(i -> i.setAlbum(this));
        }
        this.songs = songs;
    }

    public Artiste getArtiste() {
        return this.artiste;
    }

    public Album artiste(Artiste artiste) {
        this.setArtiste(artiste);
        return this;
    }

    public void setArtiste(Artiste artiste) {
        this.artiste = artiste;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Album)) {
            return false;
        }
        return id != null && id.equals(((Album) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Album{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", releaseDate='" + getReleaseDate() + "'" +
            "}";
    }
}
