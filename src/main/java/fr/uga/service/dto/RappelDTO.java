package fr.uga.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.uga.domain.Rappel} entity.
 */
@Schema(description = "Rappel qu'un docteur souhaite avoir à une date donnée")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RappelDTO implements Serializable {

    private Long id;

    /**
     * Date où le rappel doit être effectué
     */
    @NotNull
    @Schema(description = "Date où le rappel doit être effectué", required = true)
    private ZonedDateTime date;

    /**
     * Descriptif textuel de l'objectif du rappel
     */
    @NotNull
    @Size(max = 2000)
    @Schema(description = "Descriptif textuel de l'objectif du rappel", required = true)
    private String description;

    /**
     * Flag de suppression
     */
    @Schema(description = "Flag de suppression")
    private Boolean deleted;

    private DocteurDTO docteur;

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
        if (!(o instanceof RappelDTO)) {
            return false;
        }

        RappelDTO rappelDTO = (RappelDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rappelDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RappelDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", deleted='" + getDeleted() + "'" +
            ", docteur=" + getDocteur() +
            "}";
    }
}
