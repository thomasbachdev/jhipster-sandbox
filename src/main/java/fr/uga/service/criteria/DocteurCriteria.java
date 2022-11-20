package fr.uga.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link fr.uga.domain.Docteur} entity. This class is used
 * in {@link fr.uga.web.rest.DocteurResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /docteurs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DocteurCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter deleted;

    private LongFilter userId;

    private LongFilter demandeId;

    private LongFilter rappelId;

    private Boolean distinct;

    public DocteurCriteria() {}

    public DocteurCriteria(DocteurCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.deleted = other.deleted == null ? null : other.deleted.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.demandeId = other.demandeId == null ? null : other.demandeId.copy();
        this.rappelId = other.rappelId == null ? null : other.rappelId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DocteurCriteria copy() {
        return new DocteurCriteria(this);
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

    public LongFilter getRappelId() {
        return rappelId;
    }

    public LongFilter rappelId() {
        if (rappelId == null) {
            rappelId = new LongFilter();
        }
        return rappelId;
    }

    public void setRappelId(LongFilter rappelId) {
        this.rappelId = rappelId;
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
        final DocteurCriteria that = (DocteurCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(deleted, that.deleted) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(demandeId, that.demandeId) &&
            Objects.equals(rappelId, that.rappelId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deleted, userId, demandeId, rappelId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DocteurCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (deleted != null ? "deleted=" + deleted + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (demandeId != null ? "demandeId=" + demandeId + ", " : "") +
            (rappelId != null ? "rappelId=" + rappelId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
