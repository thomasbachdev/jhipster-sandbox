package fr.uga.service;

import fr.uga.domain.*; // for static metamodels
import fr.uga.domain.Demande;
import fr.uga.repository.DemandeRepository;
import fr.uga.service.criteria.DemandeCriteria;
import fr.uga.service.dto.DemandeDTO;
import fr.uga.service.mapper.DemandeMapper;
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
 * Service for executing complex queries for {@link Demande} entities in the database.
 * The main input is a {@link DemandeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DemandeDTO} or a {@link Page} of {@link DemandeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DemandeQueryService extends QueryService<Demande> {

    private final Logger log = LoggerFactory.getLogger(DemandeQueryService.class);

    private final DemandeRepository demandeRepository;

    private final DemandeMapper demandeMapper;

    public DemandeQueryService(DemandeRepository demandeRepository, DemandeMapper demandeMapper) {
        this.demandeRepository = demandeRepository;
        this.demandeMapper = demandeMapper;
    }

    /**
     * Return a {@link List} of {@link DemandeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DemandeDTO> findByCriteria(DemandeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Demande> specification = createSpecification(criteria);
        return demandeMapper.toDto(demandeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DemandeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DemandeDTO> findByCriteria(DemandeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Demande> specification = createSpecification(criteria);
        return demandeRepository.findAll(specification, page).map(demandeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DemandeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Demande> specification = createSpecification(criteria);
        return demandeRepository.count(specification);
    }

    /**
     * Function to convert {@link DemandeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Demande> createSpecification(DemandeCriteria criteria) {
        Specification<Demande> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Demande_.id));
            }
            if (criteria.getDateEmition() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateEmition(), Demande_.dateEmition));
            }
            if (criteria.getDateLimite() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateLimite(), Demande_.dateLimite));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Demande_.description));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), Demande_.deleted));
            }
            if (criteria.getResidentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getResidentId(), root -> root.join(Demande_.resident, JoinType.LEFT).get(Resident_.id))
                    );
            }
            if (criteria.getDocteurId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDocteurId(), root -> root.join(Demande_.docteur, JoinType.LEFT).get(Docteur_.id))
                    );
            }
        }
        return specification;
    }
}
