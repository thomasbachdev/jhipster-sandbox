package fr.uga.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link fr.uga.domain.Soignant} entity. This class is used
 * in {@link fr.uga.web.rest.SoignantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /soignants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SoignantCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter deleted;

    private LongFilter userId;

    private LongFilter etablissementId;

    private LongFilter examenId;

    private Boolean distinct;

    public SoignantCriteria() {}

    public SoignantCriteria(SoignantCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.deleted = other.deleted == null ? null : other.deleted.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.etablissementId = other.etablissementId == null ? null : other.etablissementId.copy();
        this.examenId = other.examenId == null ? null : other.examenId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SoignantCriteria copy() {
        return new SoignantCriteria(this);
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

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final SoignantCriteria that = (SoignantCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(deleted, that.deleted) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(etablissementId, that.etablissementId) &&
            Objects.equals(examenId, that.examenId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deleted, userId, etablissementId, examenId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SoignantCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (deleted != null ? "deleted=" + deleted + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (etablissementId != null ? "etablissementId=" + etablissementId + ", " : "") +
            (examenId != null ? "examenId=" + examenId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
