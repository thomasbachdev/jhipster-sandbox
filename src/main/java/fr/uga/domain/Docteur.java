package fr.uga.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Informations concernant le docteur
 */
@Entity
@Table(name = "docteur")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Docteur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Flag de suppression
     */
    @Column(name = "deleted")
    private Boolean deleted;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "docteur")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "resident", "docteur" }, allowSetters = true)
    private Set<Demande> demandes = new HashSet<>();

    @OneToMany(mappedBy = "docteur")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "docteur" }, allowSetters = true)
    private Set<Rappel> rappels = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Docteur id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public Docteur deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Docteur user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Demande> getDemandes() {
        return this.demandes;
    }

    public void setDemandes(Set<Demande> demandes) {
        if (this.demandes != null) {
            this.demandes.forEach(i -> i.setDocteur(null));
        }
        if (demandes != null) {
            demandes.forEach(i -> i.setDocteur(this));
        }
        this.demandes = demandes;
    }

    public Docteur demandes(Set<Demande> demandes) {
        this.setDemandes(demandes);
        return this;
    }

    public Docteur addDemande(Demande demande) {
        this.demandes.add(demande);
        demande.setDocteur(this);
        return this;
    }

    public Docteur removeDemande(Demande demande) {
        this.demandes.remove(demande);
        demande.setDocteur(null);
        return this;
    }

    public Set<Rappel> getRappels() {
        return this.rappels;
    }

    public void setRappels(Set<Rappel> rappels) {
        if (this.rappels != null) {
            this.rappels.forEach(i -> i.setDocteur(null));
        }
        if (rappels != null) {
            rappels.forEach(i -> i.setDocteur(this));
        }
        this.rappels = rappels;
    }

    public Docteur rappels(Set<Rappel> rappels) {
        this.setRappels(rappels);
        return this;
    }

    public Docteur addRappel(Rappel rappel) {
        this.rappels.add(rappel);
        rappel.setDocteur(this);
        return this;
    }

    public Docteur removeRappel(Rappel rappel) {
        this.rappels.remove(rappel);
        rappel.setDocteur(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Docteur)) {
            return false;
        }
        return id != null && id.equals(((Docteur) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Docteur{" +
            "id=" + getId() +
            ", deleted='" + getDeleted() + "'" +
            "}";
    }
}
