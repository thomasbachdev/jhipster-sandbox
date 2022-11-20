package fr.uga.service;

import fr.uga.domain.*; // for static metamodels
import fr.uga.domain.Resident;
import fr.uga.repository.ResidentRepository;
import fr.uga.service.criteria.ResidentCriteria;
import fr.uga.service.dto.ResidentDTO;
import fr.uga.service.mapper.ResidentMapper;
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
 * Service for executing complex queries for {@link Resident} entities in the database.
 * The main input is a {@link ResidentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ResidentDTO} or a {@link Page} of {@link ResidentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ResidentQueryService extends QueryService<Resident> {

    private final Logger log = LoggerFactory.getLogger(ResidentQueryService.class);

    private final ResidentRepository residentRepository;

    private final ResidentMapper residentMapper;

    public ResidentQueryService(ResidentRepository residentRepository, ResidentMapper residentMapper) {
        this.residentRepository = residentRepository;
        this.residentMapper = residentMapper;
    }

    /**
     * Return a {@link List} of {@link ResidentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ResidentDTO> findByCriteria(ResidentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Resident> specification = createSpecification(criteria);
        return residentMapper.toDto(residentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ResidentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ResidentDTO> findByCriteria(ResidentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Resident> specification = createSpecification(criteria);
        return residentRepository.findAll(specification, page).map(residentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ResidentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Resident> specification = createSpecification(criteria);
        return residentRepository.count(specification);
    }

    /**
     * Function to convert {@link ResidentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Resident> createSpecification(ResidentCriteria criteria) {
        Specification<Resident> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Resident_.id));
            }
            if (criteria.getNumero() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumero(), Resident_.numero));
            }
            if (criteria.getNom() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNom(), Resident_.nom));
            }
            if (criteria.getPrenom() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPrenom(), Resident_.prenom));
            }
            if (criteria.getDateNaissance() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateNaissance(), Resident_.dateNaissance));
            }
            if (criteria.getSexe() != null) {
                specification = specification.and(buildSpecification(criteria.getSexe(), Resident_.sexe));
            }
            if (criteria.getDateArrivee() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateArrivee(), Resident_.dateArrivee));
            }
            if (criteria.getChambre() != null) {
                specification = specification.and(buildStringSpecification(criteria.getChambre(), Resident_.chambre));
            }
            if (criteria.getTaille() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTaille(), Resident_.taille));
            }
            if (criteria.getDenutrition() != null) {
                specification = specification.and(buildSpecification(criteria.getDenutrition(), Resident_.denutrition));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), Resident_.deleted));
            }
            if (criteria.getEtablissementId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getEtablissementId(),
                            root -> root.join(Resident_.etablissement, JoinType.LEFT).get(Etablissement_.id)
                        )
                    );
            }
            if (criteria.getDemandeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDemandeId(), root -> root.join(Resident_.demandes, JoinType.LEFT).get(Demande_.id))
                    );
            }
            if (criteria.getExamenId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getExamenId(), root -> root.join(Resident_.examen, JoinType.LEFT).get(Examen_.id))
                    );
            }
        }
        return specification;
    }
}
