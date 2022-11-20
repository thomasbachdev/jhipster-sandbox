package fr.uga.service.criteria;

import fr.uga.domain.enumeration.Sexe;
import fr.uga.domain.enumeration.StadeDenutrition;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link fr.uga.domain.Resident} entity. This class is used
 * in {@link fr.uga.web.rest.ResidentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /residents?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResidentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Sexe
     */
    public static class SexeFilter extends Filter<Sexe> {

        public SexeFilter() {}

        public SexeFilter(SexeFilter filter) {
            super(filter);
        }

        @Override
        public SexeFilter copy() {
            return new SexeFilter(this);
        }
    }

    /**
     * Class for filtering StadeDenutrition
     */
    public static class StadeDenutritionFilter extends Filter<StadeDenutrition> {

        public StadeDenutritionFilter() {}

        public StadeDenutritionFilter(StadeDenutritionFilter filter) {
            super(filter);
        }

        @Override
        public StadeDenutritionFilter copy() {
            return new StadeDenutritionFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter numero;

    private StringFilter nom;

    private StringFilter prenom;

    private LocalDateFilter dateNaissance;

    private SexeFilter sexe;

    private LocalDateFilter dateArrivee;

    private StringFilter chambre;

    private FloatFilter taille;

    private StadeDenutritionFilter denutrition;

    private BooleanFilter deleted;

    private LongFilter etablissementId;

    private LongFilter demandeId;

    private LongFilter examenId;

    private Boolean distinct;

    public ResidentCriteria() {}

    public ResidentCriteria(ResidentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.numero = other.numero == null ? null : other.numero.copy();
        this.nom = other.nom == null ? null : other.nom.copy();
        this.prenom = other.prenom == null ? null : other.prenom.copy();
        this.dateNaissance = other.dateNaissance == null ? null : other.dateNaissance.copy();
        this.sexe = other.sexe == null ? null : other.sexe.copy();
        this.dateArrivee = other.dateArrivee == null ? null : other.dateArrivee.copy();
        this.chambre = other.chambre == null ? null : other.chambre.copy();
        this.taille = other.taille == null ? null : other.taille.copy();
        this.denutrition = other.denutrition == null ? null : other.denutrition.copy();
        this.deleted = other.deleted == null ? null : other.deleted.copy();
        this.etablissementId = other.etablissementId == null ? null : other.etablissementId.copy();
        this.demandeId = other.demandeId == null ? null : other.demandeId.copy();
        this.examenId = other.examenId == null ? null : other.examenId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ResidentCriteria copy() {
        return new ResidentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getNumero() {
        return numero;
    }

    public IntegerFilter numero() {
        if (numero == null) {
            numero = new IntegerFilter();
        }
        return numero;
    }

    public void setNumero(IntegerFilter numero) {
        this.numero = numero;
    }

    public StringFilter getNom() {
        return nom;
    }

    public StringFilter nom() {
        if (nom == null) {
            nom = new StringFilter();
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public StringFilter getPrenom() {
        return prenom;
    }

    public StringFilter prenom() {
        if (prenom == null) {
            prenom = new StringFilter();
        }
        return prenom;
    }

    public void setPrenom(StringFilter prenom) {
        this.prenom = prenom;
    }

    public LocalDateFilter getDateNaissance() {
        return dateNaissance;
    }

    public LocalDateFilter dateNaissance() {
        if (dateNaissance == null) {
            dateNaissance = new LocalDateFilter();
        }
        return dateNaissance;
    }

    public void setDateNaissance(LocalDateFilter dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public SexeFilter getSexe() {
        return sexe;
    }

    public SexeFilter sexe() {
        if (sexe == null) {
            sexe = new SexeFilter();
        }
        return sexe;
    }

    public void setSexe(SexeFilter sexe) {
        this.sexe = sexe;
    }

    public LocalDateFilter getDateArrivee() {
        return dateArrivee;
    }

    public LocalDateFilter dateArrivee() {
        if (dateArrivee == null) {
            dateArrivee = new LocalDateFilter();
        }
        return dateArrivee;
    }

    public void setDateArrivee(LocalDateFilter dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public StringFilter getChambre() {
        return chambre;
    }

    public StringFilter chambre() {
        if (chambre == null) {
            chambre = new StringFilter();
        }
        return chambre;
    }

    public void setChambre(StringFilter chambre) {
        this.chambre = chambre;
    }

    public FloatFilter getTaille() {
        return taille;
    }

    public FloatFilter taille() {
        if (taille == null) {
            taille = new FloatFilter();
        }
        return taille;
    }

    public void setTaille(FloatFilter taille) {
        this.taille = taille;
    }

    public StadeDenutritionFilter getDenutrition() {
        return denutrition;
    }

    public StadeDenutritionFilter denutrition() {
        if (denutrition == null) {
            denutrition = new StadeDenutritionFilter();
        }
        return denutrition;
    }

    public void setDenutrition(StadeDenutritionFilter denutrition) {
        this.denutrition = denutrition;
    }

    public BooleanFilter getDeleted() {
        return deleted;
    }

    public BooleanFilter deleted() {
        if (deleted == null) {
            deleted = new BooleanFilter();
        }
        return deleted;
    }

    public void setDeleted(BooleanFilter deleted) {
        this.deleted = deleted;
    }

    public LongFilter getEtablissementId() {
        return etablissementId;
    }

    public LongFilter etablissementId() {
        if (etablissementId == null) {
            etablissementId = new LongFilter();
        }
        return etablissementId;
    }

    public void setEtablissementId(LongFilter etablissementId) {
        this.etablissementId = etablissementId;
    }

    public LongFilter getDemandeId() {
        return demandeId;
    }

    public LongFilter demandeId() {
        if (demandeId == null) {
            demandeId = new LongFilter();
        }
        return demandeId;
    }

    public void setDemandeId(LongFilter demandeId) {
        this.demandeId = demandeId;
    }

    public LongFilter getExamenId() {
        return examenId;
    }

    public LongFilter examenId() {
        if (examenId == null) {
            examenId = new LongFilter();
        }
        return examenId;
    }

    public void setExamenId(LongFilter examenId) {
        this.examenId = examenId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ResidentCriteria that = (ResidentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(numero, that.numero) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(prenom, that.prenom) &&
            Objects.equals(dateNaissance, that.dateNaissance) &&
            Objects.equals(sexe, that.sexe) &&
            Objects.equals(dateArrivee, that.dateArrivee) &&
            Objects.equals(chambre, that.chambre) &&
            Objects.equals(taille, that.taille) &&
            Objects.equals(denutrition, that.denutrition) &&
            Objects.equals(deleted, that.deleted) &&
            Objects.equals(etablissementId, that.etablissementId) &&
            Objects.equals(demandeId, that.demandeId) &&
            Objects.equals(examenId, that.examenId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            numero,
            nom,
            prenom,
            dateNaissance,
            sexe,
            dateArrivee,
            chambre,
            taille,
            denutrition,
            deleted,
            etablissementId,
            demandeId,
            examenId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResidentCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (numero != null ? "numero=" + numero + ", " : "") +
            (nom != null ? "nom=" + nom + ", " : "") +
            (prenom != null ? "prenom=" + prenom + ", " : "") +
            (dateNaissance != null ? "dateNaissance=" + dateNaissance + ", " : "") +
            (sexe != null ? "sexe=" + sexe + ", " : "") +
            (dateArrivee != null ? "dateArrivee=" + dateArrivee + ", " : "") +
            (chambre != null ? "chambre=" + chambre + ", " : "") +
            (taille != null ? "taille=" + taille + ", " : "") +
            (denutrition != null ? "denutrition=" + denutrition + ", " : "") +
            (deleted != null ? "deleted=" + deleted + ", " : "") +
            (etablissementId != null ? "etablissementId=" + etablissementId + ", " : "") +
            (demandeId != null ? "demandeId=" + demandeId + ", " : "") +
            (examenId != null ? "examenId=" + examenId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
