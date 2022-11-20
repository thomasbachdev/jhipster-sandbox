package fr.uga.service;

import fr.uga.domain.*; // for static metamodels
import fr.uga.domain.Soignant;
import fr.uga.repository.SoignantRepository;
import fr.uga.service.criteria.SoignantCriteria;
import fr.uga.service.dto.SoignantDTO;
import fr.uga.service.mapper.SoignantMapper;
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
 * Service for executing complex queries for {@link Soignant} entities in the database.
 * The main input is a {@link SoignantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SoignantDTO} or a {@link Page} of {@link SoignantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SoignantQueryService extends QueryService<Soignant> {

    private final Logger log = LoggerFactory.getLogger(SoignantQueryService.class);

    private final SoignantRepository soignantRepository;

    private final SoignantMapper soignantMapper;

    public SoignantQueryService(SoignantRepository soignantRepository, SoignantMapper soignantMapper) {
        this.soignantRepository = soignantRepository;
        this.soignantMapper = soignantMapper;
    }

    /**
     * Return a {@link List} of {@link SoignantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SoignantDTO> findByCriteria(SoignantCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Soignant> specification = createSpecification(criteria);
        return soignantMapper.toDto(soignantRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SoignantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SoignantDTO> findByCriteria(SoignantCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Soignant> specification = createSpecification(criteria);
        return soignantRepository.findAll(specification, page).map(soignantMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SoignantCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Soignant> specification = createSpecification(criteria);
        return soignantRepository.count(specification);
    }

    /**
     * Function to convert {@link SoignantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Soignant> createSpecification(SoignantCriteria criteria) {
        Specification<Soignant> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Soignant_.id));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), Soignant_.deleted));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Soignant_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getEtablissementId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getEtablissementId(),
                            root -> root.join(Soignant_.etablissement, JoinType.LEFT).get(Etablissement_.id)
                        )
                    );
            }
            if (criteria.getExamenId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getExamenId(), root -> root.join(Soignant_.examen, JoinType.LEFT).get(Examen_.id))
                    );
            }
        }
        return specification;
    }
}
