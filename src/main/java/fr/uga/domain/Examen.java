package fr.uga.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Informations résultantes d'un examen, concernant un résident
 */
@Entity
@Table(name = "examen")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Examen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Date de réalisation de l'examen
     */
    @NotNull
    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    /**
     * Poids du résident mesuré lors de l'examen
     */
    @DecimalMin(value = "0")
    @Column(name = "poids")
    private Float poids;

    /**
     * Taux d'albumine du résident mesuré lors de l'examen
     */
    @DecimalMin(value = "0")
    @Column(name = "albumine")
    private Float albumine;

    /**
     * Indice de masse corporelle du résident, le jour de l'examen
     */
    @Column(name = "imc")
    private Float imc;

    /**
     * EPA du résident mesuré lors de l'examen
     */
    @Min(value = 0)
    @Max(value = 10)
    @Column(name = "epa")
    private Integer epa;

    /**
     * Commentaire du soignant sur l'examen réalisé
     */
    @Size(max = 2000)
    @Column(name = "commentaire", length = 2000)
    private String commentaire;

    /**
     * Flag de suppression
     */
    @Column(name = "deleted")
    private Boolean deleted;

    @ManyToOne
    @JsonIgnoreProperties(value = { "etablissement", "demandes", "examen" }, allowSetters = true)
    private Resident resident;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "etablissement", "examen" }, allowSetters = true)
    private Soignant soignant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Examen id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public Examen date(ZonedDateTime date) {
        this.setDate(date);
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Float getPoids() {
        return this.poids;
    }

    public Examen poids(Float poids) {
        this.setPoids(poids);
        return this;
    }

    public void setPoids(Float poids) {
        this.poids = poids;
    }

    public Float getAlbumine() {
        return this.albumine;
    }

    public Examen albumine(Float albumine) {
        this.setAlbumine(albumine);
        return this;
    }

    public void setAlbumine(Float albumine) {
        this.albumine = albumine;
    }

    public Float getImc() {
        return this.imc;
    }

    public Examen imc(Float imc) {
        this.setImc(imc);
        return this;
    }

    public void setImc(Float imc) {
        this.imc = imc;
    }

    public Integer getEpa() {
        return this.epa;
    }

    public Examen epa(Integer epa) {
        this.setEpa(epa);
        return this;
    }

    public void setEpa(Integer epa) {
        this.epa = epa;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public Examen commentaire(String commentaire) {
        this.setCommentaire(commentaire);
        return this;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public Examen deleted(Boolean deleted) {
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

    public Examen resident(Resident resident) {
        this.setResident(resident);
        return this;
    }

    public Soignant getSoignant() {
        return this.soignant;
    }

    public void setSoignant(Soignant soignant) {
        this.soignant = soignant;
    }

    public Examen soignant(Soignant soignant) {
        this.setSoignant(soignant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Examen)) {
            return false;
        }
        return id != null && id.equals(((Examen) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Examen{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", poids=" + getPoids() +
            ", albumine=" + getAlbumine() +
            ", imc=" + getImc() +
            ", epa=" + getEpa() +
            ", commentaire='" + getCommentaire() + "'" +
            ", deleted='" + getDeleted() + "'" +
            "}";
    }
}
