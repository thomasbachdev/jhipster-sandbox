package fr.uga.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Informations concernant le soignant
 */
@Entity
@Table(name = "soignant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Soignant implements Serializable {

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

    @ManyToOne
    @JsonIgnoreProperties(value = { "residents", "soignants" }, allowSetters = true)
    private Etablissement etablissement;

    @OneToMany(mappedBy = "soignant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "resident", "soignant" }, allowSetters = true)
    private Set<Examen> examen = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Soignant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public Soignant deleted(Boolean deleted) {
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

    public Soignant user(User user) {
        this.setUser(user);
        return this;
    }

    public Etablissement getEtablissement() {
        return this.etablissement;
    }

    public void setEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement;
    }

    public Soignant etablissement(Etablissement etablissement) {
        this.setEtablissement(etablissement);
        return this;
    }

    public Set<Examen> getExamen() {
        return this.examen;
    }

    public void setExamen(Set<Examen> examen) {
        if (this.examen != null) {
            this.examen.forEach(i -> i.setSoignant(null));
        }
        if (examen != null) {
            examen.forEach(i -> i.setSoignant(this));
        }
        this.examen = examen;
    }

    public Soignant examen(Set<Examen> examen) {
        this.setExamen(examen);
        return this;
    }

    public Soignant addExamen(Examen examen) {
        this.examen.add(examen);
        examen.setSoignant(this);
        return this;
    }

    public Soignant removeExamen(Examen examen) {
        this.examen.remove(examen);
        examen.setSoignant(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Soignant)) {
            return false;
        }
        return id != null && id.equals(((Soignant) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Soignant{" +
            "id=" + getId() +
            ", deleted='" + getDeleted() + "'" +
            "}";
    }
}
