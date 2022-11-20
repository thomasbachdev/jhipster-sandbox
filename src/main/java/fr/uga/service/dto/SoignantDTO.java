package fr.uga.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link fr.uga.domain.Soignant} entity.
 */
@Schema(description = "Informations concernant le soignant")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SoignantDTO implements Serializable {

    private Long id;

    /**
     * Flag de suppression
     */
    @Schema(description = "Flag de suppression")
    private Boolean deleted;

    private UserDTO user;

    private EtablissementDTO etablissement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public EtablissementDTO getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(EtablissementDTO etablissement) {
        this.etablissement = etablissement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SoignantDTO)) {
            return false;
        }

        SoignantDTO soignantDTO = (SoignantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, soignantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SoignantDTO{" +
            "id=" + getId() +
            ", deleted='" + getDeleted() + "'" +
            ", user=" + getUser() +
            ", etablissement=" + getEtablissement() +
            "}";
    }
}
