package fr.uga.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.uga.domain.Examen} entity.
 */
@Schema(description = "Informations résultantes d'un examen, concernant un résident")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamenDTO implements Serializable {

    private Long id;

    /**
     * Date de réalisation de l'examen
     */
    @NotNull
    @Schema(description = "Date de réalisation de l'examen", required = true)
    private ZonedDateTime date;

    /**
     * Poids du résident mesuré lors de l'examen
     */
    @DecimalMin(value = "0")
    @Schema(description = "Poids du résident mesuré lors de l'examen")
    private Float poids;

    /**
     * Taux d'albumine du résident mesuré lors de l'examen
     */
    @DecimalMin(value = "0")
    @Schema(description = "Taux d'albumine du résident mesuré lors de l'examen")
    private Float albumine;

    /**
     * Indice de masse corporelle du résident, le jour de l'examen
     */
    @Schema(description = "Indice de masse corporelle du résident, le jour de l'examen")
    private Float imc;

    /**
     * EPA du résident mesuré lors de l'examen
     */
    @Min(value = 0)
    @Max(value = 10)
    @Schema(description = "EPA du résident mesuré lors de l'examen")
    private Integer epa;

    /**
     * Commentaire du soignant sur l'examen réalisé
     */
    @Size(max = 2000)
    @Schema(description = "Commentaire du soignant sur l'examen réalisé")
    private String commentaire;

    /**
     * Flag de suppression
     */
    @Schema(description = "Flag de suppression")
    private Boolean deleted;

    private ResidentDTO resident;

    private SoignantDTO soignant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Float getPoids() {
        return poids;
    }

    public void setPoids(Float poids) {
        this.poids = poids;
    }

    public Float getAlbumine() {
        return albumine;
    }

    public void setAlbumine(Float albumine) {
        this.albumine = albumine;
    }

    public Float getImc() {
        return imc;
    }

    public void setImc(Float imc) {
        this.imc = imc;
    }

    public Integer getEpa() {
        return epa;
    }

    public void setEpa(Integer epa) {
        this.epa = epa;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public ResidentDTO getResident() {
        return resident;
    }

    public void setResident(ResidentDTO resident) {
        this.resident = resident;
    }

    public SoignantDTO getSoignant() {
        return soignant;
    }

    public void setSoignant(SoignantDTO soignant) {
        this.soignant = soignant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamenDTO)) {
            return false;
        }

        ExamenDTO examenDTO = (ExamenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, examenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamenDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", poids=" + getPoids() +
            ", albumine=" + getAlbumine() +
            ", imc=" + getImc() +
            ", epa=" + getEpa() +
            ", commentaire='" + getCommentaire() + "'" +
            ", deleted='" + getDeleted() + "'" +
            ", resident=" + getResident() +
            ", soignant=" + getSoignant() +
            "}";
    }
}
