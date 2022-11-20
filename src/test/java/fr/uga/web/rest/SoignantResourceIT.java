package fr.uga.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.uga.IntegrationTest;
import fr.uga.domain.Etablissement;
import fr.uga.domain.Examen;
import fr.uga.domain.Soignant;
import fr.uga.domain.User;
import fr.uga.repository.SoignantRepository;
import fr.uga.service.criteria.SoignantCriteria;
import fr.uga.service.dto.SoignantDTO;
import fr.uga.service.mapper.SoignantMapper;
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
 * Integration tests for the {@link SoignantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SoignantResourceIT {

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String ENTITY_API_URL = "/api/soignants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SoignantRepository soignantRepository;

    @Autowired
    private SoignantMapper soignantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSoignantMockMvc;

    private Soignant soignant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Soignant createEntity(EntityManager em) {
        Soignant soignant = new Soignant().deleted(DEFAULT_DELETED);
        return soignant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Soignant createUpdatedEntity(EntityManager em) {
        Soignant soignant = new Soignant().deleted(UPDATED_DELETED);
        return soignant;
    }

    @BeforeEach
    public void initTest() {
        soignant = createEntity(em);
    }

    @Test
    @Transactional
    void createSoignant() throws Exception {
        int databaseSizeBeforeCreate = soignantRepository.findAll().size();
        // Create the Soignant
        SoignantDTO soignantDTO = soignantMapper.toDto(soignant);
        restSoignantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(soignantDTO)))
            .andExpect(status().isCreated());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeCreate + 1);
        Soignant testSoignant = soignantList.get(soignantList.size() - 1);
        assertThat(testSoignant.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void createSoignantWithExistingId() throws Exception {
        // Create the Soignant with an existing ID
        soignant.setId(1L);
        SoignantDTO soignantDTO = soignantMapper.toDto(soignant);

        int databaseSizeBeforeCreate = soignantRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSoignantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(soignantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSoignants() throws Exception {
        // Initialize the database
        soignantRepository.saveAndFlush(soignant);

        // Get all the soignantList
        restSoignantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(soignant.getId().intValue())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    void getSoignant() throws Exception {
        // Initialize the database
        soignantRepository.saveAndFlush(soignant);

        // Get the soignant
        restSoignantMockMvc
            .perform(get(ENTITY_API_URL_ID, soignant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(soignant.getId().intValue()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getSoignantsByIdFiltering() throws Exception {
        // Initialize the database
        soignantRepository.saveAndFlush(soignant);

        Long id = soignant.getId();

        defaultSoignantShouldBeFound("id.equals=" + id);
        defaultSoignantShouldNotBeFound("id.notEquals=" + id);

        defaultSoignantShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSoignantShouldNotBeFound("id.greaterThan=" + id);

        defaultSoignantShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSoignantShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSoignantsByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        soignantRepository.saveAndFlush(soignant);

        // Get all the soignantList where deleted equals to DEFAULT_DELETED
        defaultSoignantShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the soignantList where deleted equals to UPDATED_DELETED
        defaultSoignantShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllSoignantsByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        soignantRepository.saveAndFlush(soignant);

        // Get all the soignantList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultSoignantShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the soignantList where deleted equals to UPDATED_DELETED
        defaultSoignantShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllSoignantsByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        soignantRepository.saveAndFlush(soignant);

        // Get all the soignantList where deleted is not null
        defaultSoignantShouldBeFound("deleted.specified=true");

        // Get all the soignantList where deleted is null
        defaultSoignantShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllSoignantsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            soignantRepository.saveAndFlush(soignant);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        soignant.setUser(user);
        soignantRepository.saveAndFlush(soignant);
        Long userId = user.getId();

        // Get all the soignantList where user equals to userId
        defaultSoignantShouldBeFound("userId.equals=" + userId);

        // Get all the soignantList where user equals to (userId + 1)
        defaultSoignantShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllSoignantsByEtablissementIsEqualToSomething() throws Exception {
        Etablissement etablissement;
        if (TestUtil.findAll(em, Etablissement.class).isEmpty()) {
            soignantRepository.saveAndFlush(soignant);
            etablissement = EtablissementResourceIT.createEntity(em);
        } else {
            etablissement = TestUtil.findAll(em, Etablissement.class).get(0);
        }
        em.persist(etablissement);
        em.flush();
        soignant.setEtablissement(etablissement);
        soignantRepository.saveAndFlush(soignant);
        Long etablissementId = etablissement.getId();

        // Get all the soignantList where etablissement equals to etablissementId
        defaultSoignantShouldBeFound("etablissementId.equals=" + etablissementId);

        // Get all the soignantList where etablissement equals to (etablissementId + 1)
        defaultSoignantShouldNotBeFound("etablissementId.equals=" + (etablissementId + 1));
    }

    @Test
    @Transactional
    void getAllSoignantsByExamenIsEqualToSomething() throws Exception {
        Examen examen;
        if (TestUtil.findAll(em, Examen.class).isEmpty()) {
            soignantRepository.saveAndFlush(soignant);
            examen = ExamenResourceIT.createEntity(em);
        } else {
            examen = TestUtil.findAll(em, Examen.class).get(0);
        }
        em.persist(examen);
        em.flush();
        soignant.addExamen(examen);
        soignantRepository.saveAndFlush(soignant);
        Long examenId = examen.getId();

        // Get all the soignantList where examen equals to examenId
        defaultSoignantShouldBeFound("examenId.equals=" + examenId);

        // Get all the soignantList where examen equals to (examenId + 1)
        defaultSoignantShouldNotBeFound("examenId.equals=" + (examenId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSoignantShouldBeFound(String filter) throws Exception {
        restSoignantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(soignant.getId().intValue())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));

        // Check, that the count call also returns 1
        restSoignantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSoignantShouldNotBeFound(String filter) throws Exception {
        restSoignantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSoignantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSoignant() throws Exception {
        // Get the soignant
        restSoignantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSoignant() throws Exception {
        // Initialize the database
        soignantRepository.saveAndFlush(soignant);

        int databaseSizeBeforeUpdate = soignantRepository.findAll().size();

        // Update the soignant
        Soignant updatedSoignant = soignantRepository.findById(soignant.getId()).get();
        // Disconnect from session so that the updates on updatedSoignant are not directly saved in db
        em.detach(updatedSoignant);
        updatedSoignant.deleted(UPDATED_DELETED);
        SoignantDTO soignantDTO = soignantMapper.toDto(updatedSoignant);

        restSoignantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, soignantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(soignantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeUpdate);
        Soignant testSoignant = soignantList.get(soignantList.size() - 1);
        assertThat(testSoignant.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void putNonExistingSoignant() throws Exception {
        int databaseSizeBeforeUpdate = soignantRepository.findAll().size();
        soignant.setId(count.incrementAndGet());

        // Create the Soignant
        SoignantDTO soignantDTO = soignantMapper.toDto(soignant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSoignantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, soignantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(soignantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSoignant() throws Exception {
        int databaseSizeBeforeUpdate = soignantRepository.findAll().size();
        soignant.setId(count.incrementAndGet());

        // Create the Soignant
        SoignantDTO soignantDTO = soignantMapper.toDto(soignant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoignantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(soignantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSoignant() throws Exception {
        int databaseSizeBeforeUpdate = soignantRepository.findAll().size();
        soignant.setId(count.incrementAndGet());

        // Create the Soignant
        SoignantDTO soignantDTO = soignantMapper.toDto(soignant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoignantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(soignantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSoignantWithPatch() throws Exception {
        // Initialize the database
        soignantRepository.saveAndFlush(soignant);

        int databaseSizeBeforeUpdate = soignantRepository.findAll().size();

        // Update the soignant using partial update
        Soignant partialUpdatedSoignant = new Soignant();
        partialUpdatedSoignant.setId(soignant.getId());

        partialUpdatedSoignant.deleted(UPDATED_DELETED);

        restSoignantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSoignant.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSoignant))
            )
            .andExpect(status().isOk());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeUpdate);
        Soignant testSoignant = soignantList.get(soignantList.size() - 1);
        assertThat(testSoignant.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void fullUpdateSoignantWithPatch() throws Exception {
        // Initialize the database
        soignantRepository.saveAndFlush(soignant);

        int databaseSizeBeforeUpdate = soignantRepository.findAll().size();

        // Update the soignant using partial update
        Soignant partialUpdatedSoignant = new Soignant();
        partialUpdatedSoignant.setId(soignant.getId());

        partialUpdatedSoignant.deleted(UPDATED_DELETED);

        restSoignantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSoignant.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSoignant))
            )
            .andExpect(status().isOk());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeUpdate);
        Soignant testSoignant = soignantList.get(soignantList.size() - 1);
        assertThat(testSoignant.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingSoignant() throws Exception {
        int databaseSizeBeforeUpdate = soignantRepository.findAll().size();
        soignant.setId(count.incrementAndGet());

        // Create the Soignant
        SoignantDTO soignantDTO = soignantMapper.toDto(soignant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSoignantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, soignantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(soignantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSoignant() throws Exception {
        int databaseSizeBeforeUpdate = soignantRepository.findAll().size();
        soignant.setId(count.incrementAndGet());

        // Create the Soignant
        SoignantDTO soignantDTO = soignantMapper.toDto(soignant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoignantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(soignantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSoignant() throws Exception {
        int databaseSizeBeforeUpdate = soignantRepository.findAll().size();
        soignant.setId(count.incrementAndGet());

        // Create the Soignant
        SoignantDTO soignantDTO = soignantMapper.toDto(soignant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoignantMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(soignantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Soignant in the database
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSoignant() throws Exception {
        // Initialize the database
        soignantRepository.saveAndFlush(soignant);

        int databaseSizeBeforeDelete = soignantRepository.findAll().size();

        // Delete the soignant
        restSoignantMockMvc
            .perform(delete(ENTITY_API_URL_ID, soignant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Soignant> soignantList = soignantRepository.findAll();
        assertThat(soignantList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
