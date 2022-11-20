package fr.uga.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link fr.uga.domain.Demande} entity. This class is used
 * in {@link fr.uga.web.rest.DemandeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /demandes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter dateEmition;

    private LocalDateFilter dateLimite;

    private StringFilter description;

    private BooleanFilter deleted;

    private LongFilter residentId;

    private LongFilter docteurId;

    private Boolean distinct;

    public DemandeCriteria() {}

    public DemandeCriteria(DemandeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.dateEmition = other.dateEmition == null ? null : other.dateEmition.copy();
        this.dateLimite = other.dateLimite == null ? null : other.dateLimite.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.deleted = other.deleted == null ? null : other.deleted.copy();
        this.residentId = other.residentId == null ? null : other.residentId.copy();
        this.docteurId = other.docteurId == null ? null : other.docteurId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DemandeCriteria copy() {
        return new DemandeCriteria(this);
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

    public ZonedDateTimeFilter getDateEmition() {
        return dateEmition;
    }

    public ZonedDateTimeFilter dateEmition() {
        if (dateEmition == null) {
            dateEmition = new ZonedDateTimeFilter();
        }
        return dateEmition;
    }

    public void setDateEmition(ZonedDateTimeFilter dateEmition) {
        this.dateEmition = dateEmition;
    }

    public LocalDateFilter getDateLimite() {
        return dateLimite;
    }

    public LocalDateFilter dateLimite() {
        if (dateLimite == null) {
            dateLimite = new LocalDateFilter();
        }
        return dateLimite;
    }

    public void setDateLimite(LocalDateFilter dateLimite) {
        this.dateLimite = dateLimite;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
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

    public LongFilter getDocteurId() {
        return docteurId;
    }

    public LongFilter docteurId() {
        if (docteurId == null) {
            docteurId = new LongFilter();
        }
        return docteurId;
    }

    public void setDocteurId(LongFilter docteurId) {
        this.docteurId = docteurId;
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
        final DemandeCriteria that = (DemandeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(dateEmition, that.dateEmition) &&
            Objects.equals(dateLimite, that.dateLimite) &&
            Objects.equals(description, that.description) &&
            Objects.equals(deleted, that.deleted) &&
            Objects.equals(residentId, that.residentId) &&
            Objects.equals(docteurId, that.docteurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateEmition, dateLimite, description, deleted, residentId, docteurId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (dateEmition != null ? "dateEmition=" + dateEmition + ", " : "") +
            (dateLimite != null ? "dateLimite=" + dateLimite + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (deleted != null ? "deleted=" + deleted + ", " : "") +
            (residentId != null ? "residentId=" + residentId + ", " : "") +
            (docteurId != null ? "docteurId=" + docteurId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
