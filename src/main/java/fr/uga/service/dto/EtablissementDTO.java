package fr.uga.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.uga.domain.Etablissement} entity.
 */
@Schema(description = "Informations sur l'établissement EHPAD")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtablissementDTO implements Serializable {

    private Long id;

    /**
     * Nom de l'établissement
     */
    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Z][a-z]+$")
    @Schema(description = "Nom de l'établissement", required = true)
    private String nom;

    /**
     * Flag de suppression
     */
    @Schema(description = "Flag de suppression")
    private Boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EtablissementDTO)) {
            return false;
        }

        EtablissementDTO etablissementDTO = (EtablissementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, etablissementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtablissementDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", deleted='" + getDeleted() + "'" +
            "}";
    }
}
