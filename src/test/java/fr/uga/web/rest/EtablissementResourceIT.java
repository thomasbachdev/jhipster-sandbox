package fr.uga.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.uga.IntegrationTest;
import fr.uga.domain.Etablissement;
import fr.uga.domain.Resident;
import fr.uga.domain.Soignant;
import fr.uga.repository.EtablissementRepository;
import fr.uga.service.criteria.EtablissementCriteria;
import fr.uga.service.dto.EtablissementDTO;
import fr.uga.service.mapper.EtablissementMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EtablissementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EtablissementResourceIT {

    private static final String DEFAULT_NOM = "Jjitrnm";
    private static final String UPDATED_NOM = "Fegevbv";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String ENTITY_API_URL = "/api/etablissements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Autowired
    private EtablissementMapper etablissementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEtablissementMockMvc;

    private Etablissement etablissement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Etablissement createEntity(EntityManager em) {
        Etablissement etablissement = new Etablissement().nom(DEFAULT_NOM).deleted(DEFAULT_DELETED);
        return etablissement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Etablissement createUpdatedEntity(EntityManager em) {
        Etablissement etablissement = new Etablissement().nom(UPDATED_NOM).deleted(UPDATED_DELETED);
        return etablissement;
    }

    @BeforeEach
    public void initTest() {
        etablissement = createEntity(em);
    }

    @Test
    @Transactional
    void createEtablissement() throws Exception {
        int databaseSizeBeforeCreate = etablissementRepository.findAll().size();
        // Create the Etablissement
        EtablissementDTO etablissementDTO = etablissementMapper.toDto(etablissement);
        restEtablissementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(etablissementDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeCreate + 1);
        Etablissement testEtablissement = etablissementList.get(etablissementList.size() - 1);
        assertThat(testEtablissement.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testEtablissement.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void createEtablissementWithExistingId() throws Exception {
        // Create the Etablissement with an existing ID
        etablissement.setId(1L);
        EtablissementDTO etablissementDTO = etablissementMapper.toDto(etablissement);

        int databaseSizeBeforeCreate = etablissementRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEtablissementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(etablissementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = etablissementRepository.findAll().size();
        // set the field null
        etablissement.setNom(null);

        // Create the Etablissement, which fails.
        EtablissementDTO etablissementDTO = etablissementMapper.toDto(etablissement);

        restEtablissementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(etablissementDTO))
            )
            .andExpect(status().isBadRequest());

        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEtablissements() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        // Get all the etablissementList
        restEtablissementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(etablissement.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    void getEtablissement() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        // Get the etablissement
        restEtablissementMockMvc
            .perform(get(ENTITY_API_URL_ID, etablissement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(etablissement.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getEtablissementsByIdFiltering() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        Long id = etablissement.getId();

        defaultEtablissementShouldBeFound("id.equals=" + id);
        defaultEtablissementShouldNotBeFound("id.notEquals=" + id);

        defaultEtablissementShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEtablissementShouldNotBeFound("id.greaterThan=" + id);

        defaultEtablissementShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEtablissementShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEtablissementsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        // Get all the etablissementList where nom equals to DEFAULT_NOM
        defaultEtablissementShouldBeFound("nom.equals=" + DEFAULT_NOM);

        // Get all the etablissementList where nom equals to UPDATED_NOM
        defaultEtablissementShouldNotBeFound("nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEtablissementsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        // Get all the etablissementList where nom in DEFAULT_NOM or UPDATED_NOM
        defaultEtablissementShouldBeFound("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM);

        // Get all the etablissementList where nom equals to UPDATED_NOM
        defaultEtablissementShouldNotBeFound("nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEtablissementsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        // Get all the etablissementList where nom is not null
        defaultEtablissementShouldBeFound("nom.specified=true");

        // Get all the etablissementList where nom is null
        defaultEtablissementShouldNotBeFound("nom.specified=false");
    }

    @Test
    @Transactional
    void getAllEtablissementsByNomContainsSomething() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        // Get all the etablissementList where nom contains DEFAULT_NOM
        defaultEtablissementShouldBeFound("nom.contains=" + DEFAULT_NOM);

        // Get all the etablissementList where nom contains UPDATED_NOM
        defaultEtablissementShouldNotBeFound("nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEtablissementsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        // Get all the etablissementList where nom does not contain DEFAULT_NOM
        defaultEtablissementShouldNotBeFound("nom.doesNotContain=" + DEFAULT_NOM);

        // Get all the etablissementList where nom does not contain UPDATED_NOM
        defaultEtablissementShouldBeFound("nom.doesNotContain=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEtablissementsByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        // Get all the etablissementList where deleted equals to DEFAULT_DELETED
        defaultEtablissementShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the etablissementList where deleted equals to UPDATED_DELETED
        defaultEtablissementShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllEtablissementsByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        // Get all the etablissementList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultEtablissementShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the etablissementList where deleted equals to UPDATED_DELETED
        defaultEtablissementShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllEtablissementsByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        // Get all the etablissementList where deleted is not null
        defaultEtablissementShouldBeFound("deleted.specified=true");

        // Get all the etablissementList where deleted is null
        defaultEtablissementShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllEtablissementsByResidentIsEqualToSomething() throws Exception {
        Resident resident;
        if (TestUtil.findAll(em, Resident.class).isEmpty()) {
            etablissementRepository.saveAndFlush(etablissement);
            resident = ResidentResourceIT.createEntity(em);
        } else {
            resident = TestUtil.findAll(em, Resident.class).get(0);
        }
        em.persist(resident);
        em.flush();
        etablissement.addResident(resident);
        etablissementRepository.saveAndFlush(etablissement);
        Long residentId = resident.getId();

        // Get all the etablissementList where resident equals to residentId
        defaultEtablissementShouldBeFound("residentId.equals=" + residentId);

        // Get all the etablissementList where resident equals to (residentId + 1)
        defaultEtablissementShouldNotBeFound("residentId.equals=" + (residentId + 1));
    }

    @Test
    @Transactional
    void getAllEtablissementsBySoignantIsEqualToSomething() throws Exception {
        Soignant soignant;
        if (TestUtil.findAll(em, Soignant.class).isEmpty()) {
            etablissementRepository.saveAndFlush(etablissement);
            soignant = SoignantResourceIT.createEntity(em);
        } else {
            soignant = TestUtil.findAll(em, Soignant.class).get(0);
        }
        em.persist(soignant);
        em.flush();
        etablissement.addSoignant(soignant);
        etablissementRepository.saveAndFlush(etablissement);
        Long soignantId = soignant.getId();

        // Get all the etablissementList where soignant equals to soignantId
        defaultEtablissementShouldBeFound("soignantId.equals=" + soignantId);

        // Get all the etablissementList where soignant equals to (soignantId + 1)
        defaultEtablissementShouldNotBeFound("soignantId.equals=" + (soignantId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEtablissementShouldBeFound(String filter) throws Exception {
        restEtablissementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(etablissement.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));

        // Check, that the count call also returns 1
        restEtablissementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEtablissementShouldNotBeFound(String filter) throws Exception {
        restEtablissementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEtablissementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEtablissement() throws Exception {
        // Get the etablissement
        restEtablissementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEtablissement() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        int databaseSizeBeforeUpdate = etablissementRepository.findAll().size();

        // Update the etablissement
        Etablissement updatedEtablissement = etablissementRepository.findById(etablissement.getId()).get();
        // Disconnect from session so that the updates on updatedEtablissement are not directly saved in db
        em.detach(updatedEtablissement);
        updatedEtablissement.nom(UPDATED_NOM).deleted(UPDATED_DELETED);
        EtablissementDTO etablissementDTO = etablissementMapper.toDto(updatedEtablissement);

        restEtablissementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, etablissementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(etablissementDTO))
            )
            .andExpect(status().isOk());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeUpdate);
        Etablissement testEtablissement = etablissementList.get(etablissementList.size() - 1);
        assertThat(testEtablissement.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEtablissement.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void putNonExistingEtablissement() throws Exception {
        int databaseSizeBeforeUpdate = etablissementRepository.findAll().size();
        etablissement.setId(count.incrementAndGet());

        // Create the Etablissement
        EtablissementDTO etablissementDTO = etablissementMapper.toDto(etablissement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtablissementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, etablissementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(etablissementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEtablissement() throws Exception {
        int databaseSizeBeforeUpdate = etablissementRepository.findAll().size();
        etablissement.setId(count.incrementAndGet());

        // Create the Etablissement
        EtablissementDTO etablissementDTO = etablissementMapper.toDto(etablissement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtablissementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(etablissementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEtablissement() throws Exception {
        int databaseSizeBeforeUpdate = etablissementRepository.findAll().size();
        etablissement.setId(count.incrementAndGet());

        // Create the Etablissement
        EtablissementDTO etablissementDTO = etablissementMapper.toDto(etablissement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtablissementMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(etablissementDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEtablissementWithPatch() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        int databaseSizeBeforeUpdate = etablissementRepository.findAll().size();

        // Update the etablissement using partial update
        Etablissement partialUpdatedEtablissement = new Etablissement();
        partialUpdatedEtablissement.setId(etablissement.getId());

        partialUpdatedEtablissement.deleted(UPDATED_DELETED);

        restEtablissementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtablissement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEtablissement))
            )
            .andExpect(status().isOk());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeUpdate);
        Etablissement testEtablissement = etablissementList.get(etablissementList.size() - 1);
        assertThat(testEtablissement.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testEtablissement.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void fullUpdateEtablissementWithPatch() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        int databaseSizeBeforeUpdate = etablissementRepository.findAll().size();

        // Update the etablissement using partial update
        Etablissement partialUpdatedEtablissement = new Etablissement();
        partialUpdatedEtablissement.setId(etablissement.getId());

        partialUpdatedEtablissement.nom(UPDATED_NOM).deleted(UPDATED_DELETED);

        restEtablissementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtablissement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEtablissement))
            )
            .andExpect(status().isOk());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeUpdate);
        Etablissement testEtablissement = etablissementList.get(etablissementList.size() - 1);
        assertThat(testEtablissement.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEtablissement.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingEtablissement() throws Exception {
        int databaseSizeBeforeUpdate = etablissementRepository.findAll().size();
        etablissement.setId(count.incrementAndGet());

        // Create the Etablissement
        EtablissementDTO etablissementDTO = etablissementMapper.toDto(etablissement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtablissementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, etablissementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(etablissementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEtablissement() throws Exception {
        int databaseSizeBeforeUpdate = etablissementRepository.findAll().size();
        etablissement.setId(count.incrementAndGet());

        // Create the Etablissement
        EtablissementDTO etablissementDTO = etablissementMapper.toDto(etablissement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtablissementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(etablissementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEtablissement() throws Exception {
        int databaseSizeBeforeUpdate = etablissementRepository.findAll().size();
        etablissement.setId(count.incrementAndGet());

        // Create the Etablissement
        EtablissementDTO etablissementDTO = etablissementMapper.toDto(etablissement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtablissementMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(etablissementDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Etablissement in the database
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEtablissement() throws Exception {
        // Initialize the database
        etablissementRepository.saveAndFlush(etablissement);

        int databaseSizeBeforeDelete = etablissementRepository.findAll().size();

        // Delete the etablissement
        restEtablissementMockMvc
            .perform(delete(ENTITY_API_URL_ID, etablissement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Etablissement> etablissementList = etablissementRepository.findAll();
        assertThat(etablissementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
