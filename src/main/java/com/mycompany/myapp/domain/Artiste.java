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
 * A Artiste.
 */
@Entity
@Table(name = "artiste")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Artiste implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "date_naiss", nullable = false)
    private LocalDate dateNaiss;

    @OneToMany(mappedBy = "artiste")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = {
        "songs",
        "artiste",
    }, allowSetters = true)
    private Set<Album> makes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Artiste id(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Artiste firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Artiste lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateNaiss() {
        return this.dateNaiss;
    }

    public Artiste dateNaiss(LocalDate dateNaiss) {
        this.dateNaiss = dateNaiss;
        return this;
    }

    public void setDateNaiss(LocalDate dateNaiss) {
        this.dateNaiss = dateNaiss;
    }

    public Set<Album> getMakes() {
        return this.makes;
    }

    public Artiste makes(Set<Album> albums) {
        this.setMakes(albums);
        return this;
    }

    public Artiste addMakes(Album album) {
        this.makes.add(album);
        album.setArtiste(this);
        return this;
    }

    public Artiste removeMakes(Album album) {
        this.makes.remove(album);
        album.setArtiste(null);
        return this;
    }

    public void setMakes(Set<Album> albums) {
        if (this.makes != null) {
            this.makes.forEach(i -> i.setArtiste(null));
        }
        if (albums != null) {
            albums.forEach(i -> i.setArtiste(this));
        }
        this.makes = albums;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Artiste)) {
            return false;
        }
        return id != null && id.equals(((Artiste) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Artiste{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", dateNaiss='" + getDateNaiss() + "'" +
            "}";
    }
}
