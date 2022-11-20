package fr.uga.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Demande concernant un résident, qu'un docteur adresse aux soignants
 */
@Entity
@Table(name = "demande")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Demande implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Date de création de la demande
     */
    @NotNull
    @Column(name = "date_emition", nullable = false)
    private ZonedDateTime dateEmition;

    /**
     * Date limite pour réaliser la demande
     */
    @NotNull
    @Column(name = "date_limite", nullable = false)
    private LocalDate dateLimite;

    /**
     * Descriptif textuel de l'objectif de la demande
     */
    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Flag de suppression
     */
    @Column(name = "deleted")
    private Boolean deleted;

    @ManyToOne
    @JsonIgnoreProperties(value = { "etablissement", "demandes", "examen" }, allowSetters = true)
    private Resident resident;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "demandes", "rappels" }, allowSetters = true)
    private Docteur docteur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Demande id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateEmition() {
        return this.dateEmition;
    }

    public Demande dateEmition(ZonedDateTime dateEmition) {
        this.setDateEmition(dateEmition);
        return this;
    }

    public void setDateEmition(ZonedDateTime dateEmition) {
        this.dateEmition = dateEmition;
    }

    public LocalDate getDateLimite() {
        return this.dateLimite;
    }

    public Demande dateLimite(LocalDate dateLimite) {
        this.setDateLimite(dateLimite);
        return this;
    }

    public void setDateLimite(LocalDate dateLimite) {
        this.dateLimite = dateLimite;
    }

    public String getDescription() {
        return this.description;
    }

    public Demande description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public Demande deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Resident getResident() {
        return this.resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Demande resident(Resident resident) {
        this.setResident(resident);
        return this;
    }

    public Docteur getDocteur() {
        return this.docteur;
    }

    public void setDocteur(Docteur docteur) {
        this.docteur = docteur;
    }

    public Demande docteur(Docteur docteur) {
        this.setDocteur(docteur);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Demande)) {
            return false;
        }
        return id != null && id.equals(((Demande) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Demande{" +
            "id=" + getId() +
            ", dateEmition='" + getDateEmition() + "'" +
            ", dateLimite='" + getDateLimite() + "'" +
            ", description='" + getDescription() + "'" +
            ", deleted='" + getDeleted() + "'" +
            "}";
    }
}
