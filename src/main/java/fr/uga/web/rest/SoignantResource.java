package fr.uga.web.rest;

import fr.uga.repository.SoignantRepository;
import fr.uga.service.SoignantQueryService;
import fr.uga.service.SoignantService;
import fr.uga.service.criteria.SoignantCriteria;
import fr.uga.service.dto.SoignantDTO;
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
 * REST controller for managing {@link fr.uga.domain.Soignant}.
 */
@RestController
@RequestMapping("/api")
public class SoignantResource {

    private final Logger log = LoggerFactory.getLogger(SoignantResource.class);

    private static final String ENTITY_NAME = "soignant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SoignantService soignantService;

    private final SoignantRepository soignantRepository;

    private final SoignantQueryService soignantQueryService;

    public SoignantResource(
        SoignantService soignantService,
        SoignantRepository soignantRepository,
        SoignantQueryService soignantQueryService
    ) {
        this.soignantService = soignantService;
        this.soignantRepository = soignantRepository;
        this.soignantQueryService = soignantQueryService;
    }

    /**
     * {@code POST  /soignants} : Create a new soignant.
     *
     * @param soignantDTO the soignantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new soignantDTO, or with status {@code 400 (Bad Request)} if the soignant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/soignants")
    public ResponseEntity<SoignantDTO> createSoignant(@RequestBody SoignantDTO soignantDTO) throws URISyntaxException {
        log.debug("REST request to save Soignant : {}", soignantDTO);
        if (soignantDTO.getId() != null) {
            throw new BadRequestAlertException("A new soignant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SoignantDTO result = soignantService.save(soignantDTO);
        return ResponseEntity
            .created(new URI("/api/soignants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /soignants/:id} : Updates an existing soignant.
     *
     * @param id the id of the soignantDTO to save.
     * @param soignantDTO the soignantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated soignantDTO,
     * or with status {@code 400 (Bad Request)} if the soignantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the soignantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/soignants/{id}")
    public ResponseEntity<SoignantDTO> updateSoignant(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SoignantDTO soignantDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Soignant : {}, {}", id, soignantDTO);
        if (soignantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, soignantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!soignantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SoignantDTO result = soignantService.update(soignantDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, soignantDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /soignants/:id} : Partial updates given fields of an existing soignant, field will ignore if it is null
     *
     * @param id the id of the soignantDTO to save.
     * @param soignantDTO the soignantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated soignantDTO,
     * or with status {@code 400 (Bad Request)} if the soignantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the soignantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the soignantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/soignants/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SoignantDTO> partialUpdateSoignant(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SoignantDTO soignantDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Soignant partially : {}, {}", id, soignantDTO);
        if (soignantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, soignantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!soignantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SoignantDTO> result = soignantService.partialUpdate(soignantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, soignantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /soignants} : get all the soignants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of soignants in body.
     */
    @GetMapping("/soignants")
    public ResponseEntity<List<SoignantDTO>> getAllSoignants(SoignantCriteria criteria) {
        log.debug("REST request to get Soignants by criteria: {}", criteria);
        List<SoignantDTO> entityList = soignantQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /soignants/count} : count all the soignants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/soignants/count")
    public ResponseEntity<Long> countSoignants(SoignantCriteria criteria) {
        log.debug("REST request to count Soignants by criteria: {}", criteria);
        return ResponseEntity.ok().body(soignantQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /soignants/:id} : get the "id" soignant.
     *
     * @param id the id of the soignantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the soignantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/soignants/{id}")
    public ResponseEntity<SoignantDTO> getSoignant(@PathVariable Long id) {
        log.debug("REST request to get Soignant : {}", id);
        Optional<SoignantDTO> soignantDTO = soignantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(soignantDTO);
    }

    /**
     * {@code DELETE  /soignants/:id} : delete the "id" soignant.
     *
     * @param id the id of the soignantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/soignants/{id}")
    public ResponseEntity<Void> deleteSoignant(@PathVariable Long id) {
        log.debug("REST request to delete Soignant : {}", id);
        soignantService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
