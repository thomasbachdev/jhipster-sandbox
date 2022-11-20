package fr.uga.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link fr.uga.domain.Examen} entity. This class is used
 * in {@link fr.uga.web.rest.ExamenResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /examen?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamenCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter date;

    private FloatFilter poids;

    private FloatFilter albumine;

    private FloatFilter imc;

    private IntegerFilter epa;

    private StringFilter commentaire;

    private BooleanFilter deleted;

    private LongFilter residentId;

    private LongFilter soignantId;

    private Boolean distinct;

    public ExamenCriteria() {}

    public ExamenCriteria(ExamenCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.poids = other.poids == null ? null : other.poids.copy();
        this.albumine = other.albumine == null ? null : other.albumine.copy();
        this.imc = other.imc == null ? null : other.imc.copy();
        this.epa = other.epa == null ? null : other.epa.copy();
        this.commentaire = other.commentaire == null ? null : other.commentaire.copy();
        this.deleted = other.deleted == null ? null : other.deleted.copy();
        this.residentId = other.residentId == null ? null : other.residentId.copy();
        this.soignantId = other.soignantId == null ? null : other.soignantId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ExamenCriteria copy() {
        return new ExamenCriteria(this);
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

    public ZonedDateTimeFilter getDate() {
        return date;
    }

    public ZonedDateTimeFilter date() {
        if (date == null) {
            date = new ZonedDateTimeFilter();
        }
        return date;
    }

    public void setDate(ZonedDateTimeFilter date) {
        this.date = date;
    }

    public FloatFilter getPoids() {
        return poids;
    }

    public FloatFilter poids() {
        if (poids == null) {
            poids = new FloatFilter();
        }
        return poids;
    }

    public void setPoids(FloatFilter poids) {
        this.poids = poids;
    }

    public FloatFilter getAlbumine() {
        return albumine;
    }

    public FloatFilter albumine() {
        if (albumine == null) {
            albumine = new FloatFilter();
        }
        return albumine;
    }

    public void setAlbumine(FloatFilter albumine) {
        this.albumine = albumine;
    }

    public FloatFilter getImc() {
        return imc;
    }

    public FloatFilter imc() {
        if (imc == null) {
            imc = new FloatFilter();
        }
        return imc;
    }

    public void setImc(FloatFilter imc) {
        this.imc = imc;
    }

    public IntegerFilter getEpa() {
        return epa;
    }

    public IntegerFilter epa() {
        if (epa == null) {
            epa = new IntegerFilter();
        }
        return epa;
    }

    public void setEpa(IntegerFilter epa) {
        this.epa = epa;
    }

    public StringFilter getCommentaire() {
        return commentaire;
    }

    public StringFilter commentaire() {
        if (commentaire == null) {
            commentaire = new StringFilter();
        }
        return commentaire;
    }

    public void setCommentaire(StringFilter commentaire) {
        this.commentaire = commentaire;
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
        final ExamenCriteria that = (ExamenCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(poids, that.poids) &&
            Objects.equals(albumine, that.albumine) &&
            Objects.equals(imc, that.imc) &&
            Objects.equals(epa, that.epa) &&
            Objects.equals(commentaire, that.commentaire) &&
            Objects.equals(deleted, that.deleted) &&
            Objects.equals(residentId, that.residentId) &&
            Objects.equals(soignantId, that.soignantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, poids, albumine, imc, epa, commentaire, deleted, residentId, soignantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamenCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (poids != null ? "poids=" + poids + ", " : "") +
            (albumine != null ? "albumine=" + albumine + ", " : "") +
            (imc != null ? "imc=" + imc + ", " : "") +
            (epa != null ? "epa=" + epa + ", " : "") +
            (commentaire != null ? "commentaire=" + commentaire + ", " : "") +
            (deleted != null ? "deleted=" + deleted + ", " : "") +
            (residentId != null ? "residentId=" + residentId + ", " : "") +
            (soignantId != null ? "soignantId=" + soignantId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
