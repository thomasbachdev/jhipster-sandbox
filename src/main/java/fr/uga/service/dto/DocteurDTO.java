package fr.uga.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link fr.uga.domain.Docteur} entity.
 */
@Schema(description = "Informations concernant le docteur")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DocteurDTO implements Serializable {

    private Long id;

    /**
     * Flag de suppression
     */
    @Schema(description = "Flag de suppression")
    private Boolean deleted;

    private UserDTO user;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocteurDTO)) {
            return false;
        }

        DocteurDTO docteurDTO = (DocteurDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, docteurDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DocteurDTO{" +
            "id=" + getId() +
            ", deleted='" + getDeleted() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
