package fr.uga.service.dto;

import fr.uga.domain.enumeration.Sexe;
import fr.uga.domain.enumeration.StadeDenutrition;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.uga.domain.Resident} entity.
 */
@Schema(description = "Informations fixes concernant un résident")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResidentDTO implements Serializable {

    private Long id;

    /**
     * Numéro correspondant aux données fournies par le client
     */
    @NotNull
    @Schema(description = "Numéro correspondant aux données fournies par le client", required = true)
    private Integer numero;

    /**
     * Nom du résident
     */
    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Z][a-z]+$")
    @Schema(description = "Nom du résident", required = true)
    private String nom;

    /**
     * Prénom du résident
     */
    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Z][a-z]+$")
    @Schema(description = "Prénom du résident", required = true)
    private String prenom;

    /**
     * Date de naissance du résident
     */
    @NotNull
    @Schema(description = "Date de naissance du résident", required = true)
    private LocalDate dateNaissance;

    /**
     * Sexe du résident
     */
    @NotNull
    @Schema(description = "Sexe du résident", required = true)
    private Sexe sexe;

    /**
     * Date d'arrivée du résident dans son établissement
     */
    @NotNull
    @Schema(description = "Date d'arrivée du résident dans son établissement", required = true)
    private LocalDate dateArrivee;

    /**
     * Numéro de chambre du résident dans son établissement
     */
    @NotNull
    @Size(max = 6)
    @Schema(description = "Numéro de chambre du résident dans son établissement", required = true)
    private String chambre;

    /**
     * Taille du résident à son entrée dans l'établissement
     */
    @NotNull
    @Schema(description = "Taille du résident à son entrée dans l'établissement", required = true)
    private Float taille;

    /**
     * Indication de stade de dénutrition du résident
     */
    @Schema(description = "Indication de stade de dénutrition du résident")
    private StadeDenutrition denutrition;

    /**
     * Flag de suppression
     */
    @Schema(description = "Flag de suppression")
    private Boolean deleted;

    private EtablissementDTO etablissement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public LocalDate getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public String getChambre() {
        return chambre;
    }

    public void setChambre(String chambre) {
        this.chambre = chambre;
    }

    public Float getTaille() {
        return taille;
    }

    public void setTaille(Float taille) {
        this.taille = taille;
    }

    public StadeDenutrition getDenutrition() {
        return denutrition;
    }

    public void setDenutrition(StadeDenutrition denutrition) {
        this.denutrition = denutrition;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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
        if (!(o instanceof ResidentDTO)) {
            return false;
        }

        ResidentDTO residentDTO = (ResidentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, residentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResidentDTO{" +
            "id=" + getId() +
            ", numero=" + getNumero() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", sexe='" + getSexe() + "'" +
            ", dateArrivee='" + getDateArrivee() + "'" +
            ", chambre='" + getChambre() + "'" +
            ", taille=" + getTaille() +
            ", denutrition='" + getDenutrition() + "'" +
            ", deleted='" + getDeleted() + "'" +
            ", etablissement=" + getEtablissement() +
            "}";
    }
}
