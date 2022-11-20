package fr.uga.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Informations sur l'établissement EHPAD
 */
@Entity
@Table(name = "etablissement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Etablissement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Nom de l'établissement
     */
    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Z][a-z]+$")
    @Column(name = "nom", length = 50, nullable = false)
    private String nom;

    /**
     * Flag de suppression
     */
    @Column(name = "deleted")
    private Boolean deleted;

    @OneToMany(mappedBy = "etablissement")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "etablissement", "demandes", "examen" }, allowSetters = true)
    private Set<Resident> residents = new HashSet<>();

    @OneToMany(mappedBy = "etablissement")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "etablissement", "examen" }, allowSetters = true)
    private Set<Soignant> soignants = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Etablissement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Etablissement nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public Etablissement deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Set<Resident> getResidents() {
        return this.residents;
    }

    public void setResidents(Set<Resident> residents) {
        if (this.residents != null) {
            this.residents.forEach(i -> i.setEtablissement(null));
        }
        if (residents != null) {
            residents.forEach(i -> i.setEtablissement(this));
        }
        this.residents = residents;
    }

    public Etablissement residents(Set<Resident> residents) {
        this.setResidents(residents);
        return this;
    }

    public Etablissement addResident(Resident resident) {
        this.residents.add(resident);
        resident.setEtablissement(this);
        return this;
    }

    public Etablissement removeResident(Resident resident) {
        this.residents.remove(resident);
        resident.setEtablissement(null);
        return this;
    }

    public Set<Soignant> getSoignants() {
        return this.soignants;
    }

    public void setSoignants(Set<Soignant> soignants) {
        if (this.soignants != null) {
            this.soignants.forEach(i -> i.setEtablissement(null));
        }
        if (soignants != null) {
            soignants.forEach(i -> i.setEtablissement(this));
        }
        this.soignants = soignants;
    }

    public Etablissement soignants(Set<Soignant> soignants) {
        this.setSoignants(soignants);
        return this;
    }

    public Etablissement addSoignant(Soignant soignant) {
        this.soignants.add(soignant);
        soignant.setEtablissement(this);
        return this;
    }

    public Etablissement removeSoignant(Soignant soignant) {
        this.soignants.remove(soignant);
        soignant.setEtablissement(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Etablissement)) {
            return false;
        }
        return id != null && id.equals(((Etablissement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Etablissement{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", deleted='" + getDeleted() + "'" +
            "}";
    }
}
