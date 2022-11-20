package fr.uga.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.uga.domain.enumeration.Sexe;
import fr.uga.domain.enumeration.StadeDenutrition;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Informations fixes concernant un résident
 */
@Entity
@Table(name = "resident")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Resident implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Numéro correspondant aux données fournies par le client
     */
    @NotNull
    @Column(name = "numero", nullable = false, unique = true)
    private Integer numero;

    /**
     * Nom du résident
     */
    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Z][a-z]+$")
    @Column(name = "nom", length = 50, nullable = false)
    private String nom;

    /**
     * Prénom du résident
     */
    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Z][a-z]+$")
    @Column(name = "prenom", length = 50, nullable = false)
    private String prenom;

    /**
     * Date de naissance du résident
     */
    @NotNull
    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    /**
     * Sexe du résident
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false)
    private Sexe sexe;

    /**
     * Date d'arrivée du résident dans son établissement
     */
    @NotNull
    @Column(name = "date_arrivee", nullable = false)
    private LocalDate dateArrivee;

    /**
     * Numéro de chambre du résident dans son établissement
     */
    @NotNull
    @Size(max = 6)
    @Column(name = "chambre", length = 6, nullable = false)
    private String chambre;

    /**
     * Taille du résident à son entrée dans l'établissement
     */
    @NotNull
    @Column(name = "taille", nullable = false)
    private Float taille;

    /**
     * Indication de stade de dénutrition du résident
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "denutrition")
    private StadeDenutrition denutrition;

    /**
     * Flag de suppression
     */
    @Column(name = "deleted")
    private Boolean deleted;

    @ManyToOne
    @JsonIgnoreProperties(value = { "residents", "soignants" }, allowSetters = true)
    private Etablissement etablissement;

    @OneToMany(mappedBy = "resident")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "resident", "docteur" }, allowSetters = true)
    private Set<Demande> demandes = new HashSet<>();

    @OneToMany(mappedBy = "resident")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "resident", "soignant" }, allowSetters = true)
    private Set<Examen> examen = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Resident id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return this.numero;
    }

    public Resident numero(Integer numero) {
        this.setNumero(numero);
        return this;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNom() {
        return this.nom;
    }

    public Resident nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Resident prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return this.dateNaissance;
    }

    public Resident dateNaissance(LocalDate dateNaissance) {
        this.setDateNaissance(dateNaissance);
        return this;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Sexe getSexe() {
        return this.sexe;
    }

    public Resident sexe(Sexe sexe) {
        this.setSexe(sexe);
        return this;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public LocalDate getDateArrivee() {
        return this.dateArrivee;
    }

    public Resident dateArrivee(LocalDate dateArrivee) {
        this.setDateArrivee(dateArrivee);
        return this;
    }

    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public String getChambre() {
        return this.chambre;
    }

    public Resident chambre(String chambre) {
        this.setChambre(chambre);
        return this;
    }

    public void setChambre(String chambre) {
        this.chambre = chambre;
    }

    public Float getTaille() {
        return this.taille;
    }

    public Resident taille(Float taille) {
        this.setTaille(taille);
        return this;
    }

    public void setTaille(Float taille) {
        this.taille = taille;
    }

    public StadeDenutrition getDenutrition() {
        return this.denutrition;
    }

    public Resident denutrition(StadeDenutrition denutrition) {
        this.setDenutrition(denutrition);
        return this;
    }

    public void setDenutrition(StadeDenutrition denutrition) {
        this.denutrition = denutrition;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public Resident deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Etablissement getEtablissement() {
        return this.etablissement;
    }

    public void setEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement;
    }

    public Resident etablissement(Etablissement etablissement) {
        this.setEtablissement(etablissement);
        return this;
    }

    public Set<Demande> getDemandes() {
        return this.demandes;
    }

    public void setDemandes(Set<Demande> demandes) {
        if (this.demandes != null) {
            this.demandes.forEach(i -> i.setResident(null));
        }
        if (demandes != null) {
            demandes.forEach(i -> i.setResident(this));
        }
        this.demandes = demandes;
    }

    public Resident demandes(Set<Demande> demandes) {
        this.setDemandes(demandes);
        return this;
    }

    public Resident addDemande(Demande demande) {
        this.demandes.add(demande);
        demande.setResident(this);
        return this;
    }

    public Resident removeDemande(Demande demande) {
        this.demandes.remove(demande);
        demande.setResident(null);
        return this;
    }

    public Set<Examen> getExamen() {
        return this.examen;
    }

    public void setExamen(Set<Examen> examen) {
        if (this.examen != null) {
            this.examen.forEach(i -> i.setResident(null));
        }
        if (examen != null) {
            examen.forEach(i -> i.setResident(this));
        }
        this.examen = examen;
    }

    public Resident examen(Set<Examen> examen) {
        this.setExamen(examen);
        return this;
    }

    public Resident addExamen(Examen examen) {
        this.examen.add(examen);
        examen.setResident(this);
        return this;
    }

    public Resident removeExamen(Examen examen) {
        this.examen.remove(examen);
        examen.setResident(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Resident)) {
            return false;
        }
        return id != null && id.equals(((Resident) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Resident{" +
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
            "}";
    }
}
