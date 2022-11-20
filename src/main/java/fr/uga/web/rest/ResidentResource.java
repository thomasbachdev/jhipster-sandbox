package fr.uga.web.rest;

import fr.uga.repository.ResidentRepository;
import fr.uga.service.ResidentQueryService;
import fr.uga.service.ResidentService;
import fr.uga.service.criteria.ResidentCriteria;
import fr.uga.service.dto.ResidentDTO;
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
 * REST controller for managing {@link fr.uga.domain.Resident}.
 */
@RestController
@RequestMapping("/api")
public class ResidentResource {

    private final Logger log = LoggerFactory.getLogger(ResidentResource.class);

    private static final String ENTITY_NAME = "resident";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResidentService residentService;

    private final ResidentRepository residentRepository;

    private final ResidentQueryService residentQueryService;

    public ResidentResource(
        ResidentService residentService,
        ResidentRepository residentRepository,
        ResidentQueryService residentQueryService
    ) {
        this.residentService = residentService;
        this.residentRepository = residentRepository;
        this.residentQueryService = residentQueryService;
    }

    /**
     * {@code POST  /residents} : Create a new resident.
     *
     * @param residentDTO the residentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new residentDTO, or with status {@code 400 (Bad Request)} if the resident has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/residents")
    public ResponseEntity<ResidentDTO> createResident(@Valid @RequestBody ResidentDTO residentDTO) throws URISyntaxException {
        log.debug("REST request to save Resident : {}", residentDTO);
        if (residentDTO.getId() != null) {
            throw new BadRequestAlertException("A new resident cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ResidentDTO result = residentService.save(residentDTO);
        return ResponseEntity
            .created(new URI("/api/residents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /residents/:id} : Updates an existing resident.
     *
     * @param id the id of the residentDTO to save.
     * @param residentDTO the residentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated residentDTO,
     * or with status {@code 400 (Bad Request)} if the residentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the residentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/residents/{id}")
    public ResponseEntity<ResidentDTO> updateResident(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ResidentDTO residentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Resident : {}, {}", id, residentDTO);
        if (residentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, residentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!residentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ResidentDTO result = residentService.update(residentDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, residentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /residents/:id} : Partial updates given fields of an existing resident, field will ignore if it is null
     *
     * @param id the id of the residentDTO to save.
     * @param residentDTO the residentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated residentDTO,
     * or with status {@code 400 (Bad Request)} if the residentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the residentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the residentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/residents/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ResidentDTO> partialUpdateResident(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ResidentDTO residentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Resident partially : {}, {}", id, residentDTO);
        if (residentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, residentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!residentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ResidentDTO> result = residentService.partialUpdate(residentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, residentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /residents} : get all the residents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of residents in body.
     */
    @GetMapping("/residents")
    public ResponseEntity<List<ResidentDTO>> getAllResidents(ResidentCriteria criteria) {
        log.debug("REST request to get Residents by criteria: {}", criteria);
        List<ResidentDTO> entityList = residentQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /residents/count} : count all the residents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/residents/count")
    public ResponseEntity<Long> countResidents(ResidentCriteria criteria) {
        log.debug("REST request to count Residents by criteria: {}", criteria);
        return ResponseEntity.ok().body(residentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /residents/:id} : get the "id" resident.
     *
     * @param id the id of the residentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the residentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/residents/{id}")
    public ResponseEntity<ResidentDTO> getResident(@PathVariable Long id) {
        log.debug("REST request to get Resident : {}", id);
        Optional<ResidentDTO> residentDTO = residentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(residentDTO);
    }

    /**
     * {@code DELETE  /residents/:id} : delete the "id" resident.
     *
     * @param id the id of the residentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/residents/{id}")
    public ResponseEntity<Void> deleteResident(@PathVariable Long id) {
        log.debug("REST request to delete Resident : {}", id);
        residentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
