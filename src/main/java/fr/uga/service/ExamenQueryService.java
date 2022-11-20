package fr.uga.service;

import fr.uga.domain.*; // for static metamodels
import fr.uga.domain.Examen;
import fr.uga.repository.ExamenRepository;
import fr.uga.service.criteria.ExamenCriteria;
import fr.uga.service.dto.ExamenDTO;
import fr.uga.service.mapper.ExamenMapper;
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
 * Service for executing complex queries for {@link Examen} entities in the database.
 * The main input is a {@link ExamenCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ExamenDTO} or a {@link Page} of {@link ExamenDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ExamenQueryService extends QueryService<Examen> {

    private final Logger log = LoggerFactory.getLogger(ExamenQueryService.class);

    private final ExamenRepository examenRepository;

    private final ExamenMapper examenMapper;

    public ExamenQueryService(ExamenRepository examenRepository, ExamenMapper examenMapper) {
        this.examenRepository = examenRepository;
        this.examenMapper = examenMapper;
    }

    /**
     * Return a {@link List} of {@link ExamenDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ExamenDTO> findByCriteria(ExamenCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Examen> specification = createSpecification(criteria);
        return examenMapper.toDto(examenRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ExamenDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ExamenDTO> findByCriteria(ExamenCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Examen> specification = createSpecification(criteria);
        return examenRepository.findAll(specification, page).map(examenMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ExamenCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Examen> specification = createSpecification(criteria);
        return examenRepository.count(specification);
    }

    /**
     * Function to convert {@link ExamenCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Examen> createSpecification(ExamenCriteria criteria) {
        Specification<Examen> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Examen_.id));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Examen_.date));
            }
            if (criteria.getPoids() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPoids(), Examen_.poids));
            }
            if (criteria.getAlbumine() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAlbumine(), Examen_.albumine));
            }
            if (criteria.getImc() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getImc(), Examen_.imc));
            }
            if (criteria.getEpa() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEpa(), Examen_.epa));
            }
            if (criteria.getCommentaire() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCommentaire(), Examen_.commentaire));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), Examen_.deleted));
            }
            if (criteria.getResidentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getResidentId(), root -> root.join(Examen_.resident, JoinType.LEFT).get(Resident_.id))
                    );
            }
            if (criteria.getSoignantId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSoignantId(), root -> root.join(Examen_.soignant, JoinType.LEFT).get(Soignant_.id))
                    );
            }
        }
        return specification;
    }
}
