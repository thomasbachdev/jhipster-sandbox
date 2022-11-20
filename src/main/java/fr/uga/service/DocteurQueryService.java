package fr.uga.service;

import fr.uga.domain.*; // for static metamodels
import fr.uga.domain.Docteur;
import fr.uga.repository.DocteurRepository;
import fr.uga.service.criteria.DocteurCriteria;
import fr.uga.service.dto.DocteurDTO;
import fr.uga.service.mapper.DocteurMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Docteur} entities in the database.
 * The main input is a {@link DocteurCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DocteurDTO} or a {@link Page} of {@link DocteurDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DocteurQueryService extends QueryService<Docteur> {

    private final Logger log = LoggerFactory.getLogger(DocteurQueryService.class);

    private final DocteurRepository docteurRepository;

    private final DocteurMapper docteurMapper;

    public DocteurQueryService(DocteurRepository docteurRepository, DocteurMapper docteurMapper) {
        this.docteurRepository = docteurRepository;
        this.docteurMapper = docteurMapper;
    }

    /**
     * Return a {@link List} of {@link DocteurDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DocteurDTO> findByCriteria(DocteurCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Docteur> specification = createSpecification(criteria);
        return docteurMapper.toDto(docteurRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DocteurDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DocteurDTO> findByCriteria(DocteurCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Docteur> specification = createSpecification(criteria);
        return docteurRepository.findAll(specification, page).map(docteurMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DocteurCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Docteur> specification = createSpecification(criteria);
        return docteurRepository.count(specification);
    }

    /**
     * Function to convert {@link DocteurCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Docteur> createSpecification(DocteurCriteria criteria) {
        Specification<Docteur> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Docteur_.id));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), Docteur_.deleted));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Docteur_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getDemandeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDemandeId(), root -> root.join(Docteur_.demandes, JoinType.LEFT).get(Demande_.id))
                    );
            }
            if (criteria.getRappelId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getRappelId(), root -> root.join(Docteur_.rappels, JoinType.LEFT).get(Rappel_.id))
                    );
            }
        }
        return specification;
    }
}
