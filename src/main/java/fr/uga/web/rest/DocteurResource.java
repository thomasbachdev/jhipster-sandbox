package fr.uga.web.rest;

import fr.uga.repository.DocteurRepository;
import fr.uga.service.DocteurQueryService;
import fr.uga.service.DocteurService;
import fr.uga.service.criteria.DocteurCriteria;
import fr.uga.service.dto.DocteurDTO;
import fr.uga.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link fr.uga.domain.Docteur}.
 */
@RestController
@RequestMapping("/api")
public class DocteurResource {

    private final Logger log = LoggerFactory.getLogger(DocteurResource.class);

    private static final String ENTITY_NAME = "docteur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DocteurService docteurService;

    private final DocteurRepository docteurRepository;

    private final DocteurQueryService docteurQueryService;

    public DocteurResource(DocteurService docteurService, DocteurRepository docteurRepository, DocteurQueryService docteurQueryService) {
        this.docteurService = docteurService;
        this.docteurRepository = docteurRepository;
        this.docteurQueryService = docteurQueryService;
    }

    /**
     * {@code POST  /docteurs} : Create a new docteur.
     *
     * @param docteurDTO the docteurDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new docteurDTO, or with status {@code 400 (Bad Request)} if the docteur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/docteurs")
    public ResponseEntity<DocteurDTO> createDocteur(@RequestBody DocteurDTO docteurDTO) throws URISyntaxException {
        log.debug("REST request to save Docteur : {}", docteurDTO);
        if (docteurDTO.getId() != null) {
            throw new BadRequestAlertException("A new docteur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DocteurDTO result = docteurService.save(docteurDTO);
        return ResponseEntity
            .created(new URI("/api/docteurs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /docteurs/:id} : Updates an existing docteur.
     *
     * @param id the id of the docteurDTO to save.
     * @param docteurDTO the docteurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated docteurDTO,
     * or with status {@code 400 (Bad Request)} if the docteurDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the docteurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/docteurs/{id}")
    public ResponseEntity<DocteurDTO> updateDocteur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DocteurDTO docteurDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Docteur : {}, {}", id, docteurDTO);
        if (docteurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, docteurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!docteurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DocteurDTO result = docteurService.update(docteurDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, docteurDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /docteurs/:id} : Partial updates given fields of an existing docteur, field will ignore if it is null
     *
     * @param id the id of the docteurDTO to save.
     * @param docteurDTO the docteurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated docteurDTO,
     * or with status {@code 400 (Bad Request)} if the docteurDTO is not valid,
     * or with status {@code 404 (Not Found)} if the docteurDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the docteurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/docteurs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DocteurDTO> partialUpdateDocteur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DocteurDTO docteurDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Docteur partially : {}, {}", id, docteurDTO);
        if (docteurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, docteurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!docteurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DocteurDTO> result = docteurService.partialUpdate(docteurDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, docteurDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /docteurs} : get all the docteurs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of docteurs in body.
     */
    @GetMapping("/docteurs")
    public ResponseEntity<List<DocteurDTO>> getAllDocteurs(DocteurCriteria criteria) {
        log.debug("REST request to get Docteurs by criteria: {}", criteria);
        List<DocteurDTO> entityList = docteurQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /docteurs/count} : count all the docteurs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/docteurs/count")
    public ResponseEntity<Long> countDocteurs(DocteurCriteria criteria) {
        log.debug("REST request to count Docteurs by criteria: {}", criteria);
        return ResponseEntity.ok().body(docteurQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /docteurs/:id} : get the "id" docteur.
     *
     * @param id the id of the docteurDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the docteurDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/docteurs/{id}")
    public ResponseEntity<DocteurDTO> getDocteur(@PathVariable Long id) {
        log.debug("REST request to get Docteur : {}", id);
        Optional<DocteurDTO> docteurDTO = docteurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(docteurDTO);
    }

    /**
     * {@code DELETE  /docteurs/:id} : delete the "id" docteur.
     *
     * @param id the id of the docteurDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/docteurs/{id}")
    public ResponseEntity<Void> deleteDocteur(@PathVariable Long id) {
        log.debug("REST request to delete Docteur : {}", id);
        docteurService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
