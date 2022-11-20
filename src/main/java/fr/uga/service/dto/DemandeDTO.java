package fr.uga.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.uga.domain.Demande} entity.
 */
@Schema(description = "Demande concernant un résident, qu'un docteur adresse aux soignants")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandeDTO implements Serializable {

    private Long id;

    /**
     * Date de création de la demande
     */
    @NotNull
    @Schema(description = "Date de création de la demande", required = true)
    private ZonedDateTime dateEmition;

    /**
     * Date limite pour réaliser la demande
     */
    @NotNull
    @Schema(description = "Date limite pour réaliser la demande", required = true)
    private LocalDate dateLimite;

    /**
     * Descriptif textuel de l'objectif de la demande
     */
    @NotNull
    @Schema(description = "Descriptif textuel de l'objectif de la demande", required = true)
    private String description;

    /**
     * Flag de suppression
     */
    @Schema(description = "Flag de suppression")
    private Boolean deleted;

    private ResidentDTO resident;

    private DocteurDTO docteur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateEmition() {
        return dateEmition;
    }

    public void setDateEmition(ZonedDateTime dateEmition) {
        this.dateEmition = dateEmition;
    }

    public LocalDate getDateLimite() {
        return dateLimite;
    }

    public void setDateLimite(LocalDate dateLimite) {
        this.dateLimite = dateLimite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public DocteurDTO getDocteur() {
        return docteur;
    }

    public void setDocteur(DocteurDTO docteur) {
        this.docteur = docteur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DemandeDTO)) {
            return false;
        }

        DemandeDTO demandeDTO = (DemandeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, demandeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandeDTO{" +
            "id=" + getId() +
            ", dateEmition='" + getDateEmition() + "'" +
            ", dateLimite='" + getDateLimite() + "'" +
            ", description='" + getDescription() + "'" +
            ", deleted='" + getDeleted() + "'" +
            ", resident=" + getResident() +
            ", docteur=" + getDocteur() +
            "}";
    }
}
