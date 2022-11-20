package fr.uga.web.rest;

import fr.uga.repository.ExamenRepository;
import fr.uga.service.ExamenQueryService;
import fr.uga.service.ExamenService;
import fr.uga.service.criteria.ExamenCriteria;
import fr.uga.service.dto.ExamenDTO;
import fr.uga.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link fr.uga.domain.Examen}.
 */
@RestController
@RequestMapping("/api")
public class ExamenResource {

    private final Logger log = LoggerFactory.getLogger(ExamenResource.class);

    private static final String ENTITY_NAME = "examen";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExamenService examenService;

    private final ExamenRepository examenRepository;

    private final ExamenQueryService examenQueryService;

    public ExamenResource(ExamenService examenService, ExamenRepository examenRepository, ExamenQueryService examenQueryService) {
        this.examenService = examenService;
        this.examenRepository = examenRepository;
        this.examenQueryService = examenQueryService;
    }

    /**
     * {@code POST  /examen} : Create a new examen.
     *
     * @param examenDTO the examenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new examenDTO, or with status {@code 400 (Bad Request)} if the examen has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/examen")
    public ResponseEntity<ExamenDTO> createExamen(@Valid @RequestBody ExamenDTO examenDTO) throws URISyntaxException {
        log.debug("REST request to save Examen : {}", examenDTO);
        if (examenDTO.getId() != null) {
            throw new BadRequestAlertException("A new examen cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ExamenDTO result = examenService.save(examenDTO);
        return ResponseEntity
            .created(new URI("/api/examen/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /examen/:id} : Updates an existing examen.
     *
     * @param id the id of the examenDTO to save.
     * @param examenDTO the examenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examenDTO,
     * or with status {@code 400 (Bad Request)} if the examenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the examenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/examen/{id}")
    public ResponseEntity<ExamenDTO> updateExamen(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExamenDTO examenDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Examen : {}, {}", id, examenDTO);
        if (examenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ExamenDTO result = examenService.update(examenDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examenDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /examen/:id} : Partial updates given fields of an existing examen, field will ignore if it is null
     *
     * @param id the id of the examenDTO to save.
     * @param examenDTO the examenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examenDTO,
     * or with status {@code 400 (Bad Request)} if the examenDTO is not valid,
     * or with status {@code 404 (Not Found)} if the examenDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the examenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/examen/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ExamenDTO> partialUpdateExamen(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExamenDTO examenDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Examen partially : {}, {}", id, examenDTO);
        if (examenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExamenDTO> result = examenService.partialUpdate(examenDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examenDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /examen} : get all the examen.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of examen in body.
     */
    @GetMapping("/examen")
    public ResponseEntity<List<ExamenDTO>> getAllExamen(ExamenCriteria criteria) {
        log.debug("REST request to get Examen by criteria: {}", criteria);
        List<ExamenDTO> entityList = examenQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /examen/count} : count all the examen.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/examen/count")
    public ResponseEntity<Long> countExamen(ExamenCriteria criteria) {
        log.debug("REST request to count Examen by criteria: {}", criteria);
        return ResponseEntity.ok().body(examenQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /examen/:id} : get the "id" examen.
     *
     * @param id the id of the examenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the examenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/examen/{id}")
    public ResponseEntity<ExamenDTO> getExamen(@PathVariable Long id) {
        log.debug("REST request to get Examen : {}", id);
        Optional<ExamenDTO> examenDTO = examenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(examenDTO);
    }

    /**
     * {@code DELETE  /examen/:id} : delete the "id" examen.
     *
     * @param id the id of the examenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/examen/{id}")
    public ResponseEntity<Void> deleteExamen(@PathVariable Long id) {
        log.debug("REST request to delete Examen : {}", id);
        examenService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
