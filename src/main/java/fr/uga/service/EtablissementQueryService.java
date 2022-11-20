package fr.uga.service;

import fr.uga.domain.*; // for static metamodels
import fr.uga.domain.Etablissement;
import fr.uga.repository.EtablissementRepository;
import fr.uga.service.criteria.EtablissementCriteria;
import fr.uga.service.dto.EtablissementDTO;
import fr.uga.service.mapper.EtablissementMapper;
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
 * Service for executing complex queries for {@link Etablissement} entities in the database.
 * The main input is a {@link EtablissementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EtablissementDTO} or a {@link Page} of {@link EtablissementDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EtablissementQueryService extends QueryService<Etablissement> {

    private final Logger log = LoggerFactory.getLogger(EtablissementQueryService.class);

    private final EtablissementRepository etablissementRepository;

    private final EtablissementMapper etablissementMapper;

    public EtablissementQueryService(EtablissementRepository etablissementRepository, EtablissementMapper etablissementMapper) {
        this.etablissementRepository = etablissementRepository;
        this.etablissementMapper = etablissementMapper;
    }

    /**
     * Return a {@link List} of {@link EtablissementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EtablissementDTO> findByCriteria(EtablissementCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Etablissement> specification = createSpecification(criteria);
        return etablissementMapper.toDto(etablissementRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EtablissementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EtablissementDTO> findByCriteria(EtablissementCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Etablissement> specification = createSpecification(criteria);
        return etablissementRepository.findAll(specification, page).map(etablissementMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EtablissementCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Etablissement> specification = createSpecification(criteria);
        return etablissementRepository.count(specification);
    }

    /**
     * Function to convert {@link EtablissementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Etablissement> createSpecification(EtablissementCriteria criteria) {
        Specification<Etablissement> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Etablissement_.id));
            }
            if (criteria.getNom() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNom(), Etablissement_.nom));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), Etablissement_.deleted));
            }
            if (criteria.getResidentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getResidentId(),
                            root -> root.join(Etablissement_.residents, JoinType.LEFT).get(Resident_.id)
                        )
                    );
            }
            if (criteria.getSoignantId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSoignantId(),
                            root -> root.join(Etablissement_.soignants, JoinType.LEFT).get(Soignant_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
