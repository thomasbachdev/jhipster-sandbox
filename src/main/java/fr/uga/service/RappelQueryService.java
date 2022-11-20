package fr.uga.service;

import fr.uga.domain.*; // for static metamodels
import fr.uga.domain.Rappel;
import fr.uga.repository.RappelRepository;
import fr.uga.service.criteria.RappelCriteria;
import fr.uga.service.dto.RappelDTO;
import fr.uga.service.mapper.RappelMapper;
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
 * Service for executing complex queries for {@link Rappel} entities in the database.
 * The main input is a {@link RappelCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RappelDTO} or a {@link Page} of {@link RappelDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RappelQueryService extends QueryService<Rappel> {

    private final Logger log = LoggerFactory.getLogger(RappelQueryService.class);

    private final RappelRepository rappelRepository;

    private final RappelMapper rappelMapper;

    public RappelQueryService(RappelRepository rappelRepository, RappelMapper rappelMapper) {
        this.rappelRepository = rappelRepository;
        this.rappelMapper = rappelMapper;
    }

    /**
     * Return a {@link List} of {@link RappelDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RappelDTO> findByCriteria(RappelCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Rappel> specification = createSpecification(criteria);
        return rappelMapper.toDto(rappelRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RappelDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RappelDTO> findByCriteria(RappelCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Rappel> specification = createSpecification(criteria);
        return rappelRepository.findAll(specification, page).map(rappelMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RappelCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Rappel> specification = createSpecification(criteria);
        return rappelRepository.count(specification);
    }

    /**
     * Function to convert {@link RappelCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Rappel> createSpecification(RappelCriteria criteria) {
        Specification<Rappel> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Rappel_.id));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Rappel_.date));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Rappel_.description));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), Rappel_.deleted));
            }
            if (criteria.getDocteurId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDocteurId(), root -> root.join(Rappel_.docteur, JoinType.LEFT).get(Docteur_.id))
                    );
            }
        }
        return specification;
    }
}
