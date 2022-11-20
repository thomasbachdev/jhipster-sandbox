package fr.uga.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link fr.uga.domain.Etablissement} entity. This class is used
 * in {@link fr.uga.web.rest.EtablissementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /etablissements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtablissementCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nom;

    private BooleanFilter deleted;

    private LongFilter residentId;

    private LongFilter soignantId;

    private Boolean distinct;

    public EtablissementCriteria() {}

    public EtablissementCriteria(EtablissementCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nom = other.nom == null ? null : other.nom.copy();
        this.deleted = other.deleted == null ? null : other.deleted.copy();
        this.residentId = other.residentId == null ? null : other.residentId.copy();
        this.soignantId = other.soignantId == null ? null : other.soignantId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EtablissementCriteria copy() {
        return new EtablissementCriteria(this);
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

    public LongFilter getResidentId() {
        return residentId;
    }

    public LongFilter residentId() {
        if (residentId == null) {
            residentId = new LongFilter();
        }
        return residentId;
    }

    public void setResidentId(LongFilter residentId) {
        this.residentId = residentId;
    }

    public LongFilter getSoignantId() {
        return soignantId;
    }

    public LongFilter soignantId() {
        if (soignantId == null) {
            soignantId = new LongFilter();
        }
        return soignantId;
    }

    public void setSoignantId(LongFilter soignantId) {
        this.soignantId = soignantId;
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
        final EtablissementCriteria that = (EtablissementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(deleted, that.deleted) &&
            Objects.equals(residentId, that.residentId) &&
            Objects.equals(soignantId, that.soignantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, deleted, residentId, soignantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtablissementCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nom != null ? "nom=" + nom + ", " : "") +
            (deleted != null ? "deleted=" + deleted + ", " : "") +
            (residentId != null ? "residentId=" + residentId + ", " : "") +
            (soignantId != null ? "soignantId=" + soignantId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
