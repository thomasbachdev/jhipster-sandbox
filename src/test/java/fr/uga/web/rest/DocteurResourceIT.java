package fr.uga.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.uga.IntegrationTest;
import fr.uga.domain.Demande;
import fr.uga.domain.Docteur;
import fr.uga.domain.Rappel;
import fr.uga.domain.User;
import fr.uga.repository.DocteurRepository;
import fr.uga.service.criteria.DocteurCriteria;
import fr.uga.service.dto.DocteurDTO;
import fr.uga.service.mapper.DocteurMapper;
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
 * Integration tests for the {@link DocteurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DocteurResourceIT {

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String ENTITY_API_URL = "/api/docteurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DocteurRepository docteurRepository;

    @Autowired
    private DocteurMapper docteurMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDocteurMockMvc;

    private Docteur docteur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Docteur createEntity(EntityManager em) {
        Docteur docteur = new Docteur().deleted(DEFAULT_DELETED);
        return docteur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Docteur createUpdatedEntity(EntityManager em) {
        Docteur docteur = new Docteur().deleted(UPDATED_DELETED);
        return docteur;
    }

    @BeforeEach
    public void initTest() {
        docteur = createEntity(em);
    }

    @Test
    @Transactional
    void createDocteur() throws Exception {
        int databaseSizeBeforeCreate = docteurRepository.findAll().size();
        // Create the Docteur
        DocteurDTO docteurDTO = docteurMapper.toDto(docteur);
        restDocteurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(docteurDTO)))
            .andExpect(status().isCreated());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeCreate + 1);
        Docteur testDocteur = docteurList.get(docteurList.size() - 1);
        assertThat(testDocteur.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void createDocteurWithExistingId() throws Exception {
        // Create the Docteur with an existing ID
        docteur.setId(1L);
        DocteurDTO docteurDTO = docteurMapper.toDto(docteur);

        int databaseSizeBeforeCreate = docteurRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocteurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(docteurDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDocteurs() throws Exception {
        // Initialize the database
        docteurRepository.saveAndFlush(docteur);

        // Get all the docteurList
        restDocteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(docteur.getId().intValue())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    void getDocteur() throws Exception {
        // Initialize the database
        docteurRepository.saveAndFlush(docteur);

        // Get the docteur
        restDocteurMockMvc
            .perform(get(ENTITY_API_URL_ID, docteur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(docteur.getId().intValue()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getDocteursByIdFiltering() throws Exception {
        // Initialize the database
        docteurRepository.saveAndFlush(docteur);

        Long id = docteur.getId();

        defaultDocteurShouldBeFound("id.equals=" + id);
        defaultDocteurShouldNotBeFound("id.notEquals=" + id);

        defaultDocteurShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDocteurShouldNotBeFound("id.greaterThan=" + id);

        defaultDocteurShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDocteurShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDocteursByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        docteurRepository.saveAndFlush(docteur);

        // Get all the docteurList where deleted equals to DEFAULT_DELETED
        defaultDocteurShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the docteurList where deleted equals to UPDATED_DELETED
        defaultDocteurShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllDocteursByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        docteurRepository.saveAndFlush(docteur);

        // Get all the docteurList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultDocteurShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the docteurList where deleted equals to UPDATED_DELETED
        defaultDocteurShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllDocteursByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        docteurRepository.saveAndFlush(docteur);

        // Get all the docteurList where deleted is not null
        defaultDocteurShouldBeFound("deleted.specified=true");

        // Get all the docteurList where deleted is null
        defaultDocteurShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllDocteursByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            docteurRepository.saveAndFlush(docteur);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        docteur.setUser(user);
        docteurRepository.saveAndFlush(docteur);
        Long userId = user.getId();

        // Get all the docteurList where user equals to userId
        defaultDocteurShouldBeFound("userId.equals=" + userId);

        // Get all the docteurList where user equals to (userId + 1)
        defaultDocteurShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllDocteursByDemandeIsEqualToSomething() throws Exception {
        Demande demande;
        if (TestUtil.findAll(em, Demande.class).isEmpty()) {
            docteurRepository.saveAndFlush(docteur);
            demande = DemandeResourceIT.createEntity(em);
        } else {
            demande = TestUtil.findAll(em, Demande.class).get(0);
        }
        em.persist(demande);
        em.flush();
        docteur.addDemande(demande);
        docteurRepository.saveAndFlush(docteur);
        Long demandeId = demande.getId();

        // Get all the docteurList where demande equals to demandeId
        defaultDocteurShouldBeFound("demandeId.equals=" + demandeId);

        // Get all the docteurList where demande equals to (demandeId + 1)
        defaultDocteurShouldNotBeFound("demandeId.equals=" + (demandeId + 1));
    }

    @Test
    @Transactional
    void getAllDocteursByRappelIsEqualToSomething() throws Exception {
        Rappel rappel;
        if (TestUtil.findAll(em, Rappel.class).isEmpty()) {
            docteurRepository.saveAndFlush(docteur);
            rappel = RappelResourceIT.createEntity(em);
        } else {
            rappel = TestUtil.findAll(em, Rappel.class).get(0);
        }
        em.persist(rappel);
        em.flush();
        docteur.addRappel(rappel);
        docteurRepository.saveAndFlush(docteur);
        Long rappelId = rappel.getId();

        // Get all the docteurList where rappel equals to rappelId
        defaultDocteurShouldBeFound("rappelId.equals=" + rappelId);

        // Get all the docteurList where rappel equals to (rappelId + 1)
        defaultDocteurShouldNotBeFound("rappelId.equals=" + (rappelId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDocteurShouldBeFound(String filter) throws Exception {
        restDocteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(docteur.getId().intValue())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));

        // Check, that the count call also returns 1
        restDocteurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDocteurShouldNotBeFound(String filter) throws Exception {
        restDocteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDocteurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDocteur() throws Exception {
        // Get the docteur
        restDocteurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDocteur() throws Exception {
        // Initialize the database
        docteurRepository.saveAndFlush(docteur);

        int databaseSizeBeforeUpdate = docteurRepository.findAll().size();

        // Update the docteur
        Docteur updatedDocteur = docteurRepository.findById(docteur.getId()).get();
        // Disconnect from session so that the updates on updatedDocteur are not directly saved in db
        em.detach(updatedDocteur);
        updatedDocteur.deleted(UPDATED_DELETED);
        DocteurDTO docteurDTO = docteurMapper.toDto(updatedDocteur);

        restDocteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, docteurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(docteurDTO))
            )
            .andExpect(status().isOk());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeUpdate);
        Docteur testDocteur = docteurList.get(docteurList.size() - 1);
        assertThat(testDocteur.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void putNonExistingDocteur() throws Exception {
        int databaseSizeBeforeUpdate = docteurRepository.findAll().size();
        docteur.setId(count.incrementAndGet());

        // Create the Docteur
        DocteurDTO docteurDTO = docteurMapper.toDto(docteur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, docteurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(docteurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDocteur() throws Exception {
        int databaseSizeBeforeUpdate = docteurRepository.findAll().size();
        docteur.setId(count.incrementAndGet());

        // Create the Docteur
        DocteurDTO docteurDTO = docteurMapper.toDto(docteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(docteurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDocteur() throws Exception {
        int databaseSizeBeforeUpdate = docteurRepository.findAll().size();
        docteur.setId(count.incrementAndGet());

        // Create the Docteur
        DocteurDTO docteurDTO = docteurMapper.toDto(docteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocteurMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(docteurDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDocteurWithPatch() throws Exception {
        // Initialize the database
        docteurRepository.saveAndFlush(docteur);

        int databaseSizeBeforeUpdate = docteurRepository.findAll().size();

        // Update the docteur using partial update
        Docteur partialUpdatedDocteur = new Docteur();
        partialUpdatedDocteur.setId(docteur.getId());

        restDocteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocteur.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDocteur))
            )
            .andExpect(status().isOk());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeUpdate);
        Docteur testDocteur = docteurList.get(docteurList.size() - 1);
        assertThat(testDocteur.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void fullUpdateDocteurWithPatch() throws Exception {
        // Initialize the database
        docteurRepository.saveAndFlush(docteur);

        int databaseSizeBeforeUpdate = docteurRepository.findAll().size();

        // Update the docteur using partial update
        Docteur partialUpdatedDocteur = new Docteur();
        partialUpdatedDocteur.setId(docteur.getId());

        partialUpdatedDocteur.deleted(UPDATED_DELETED);

        restDocteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocteur.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDocteur))
            )
            .andExpect(status().isOk());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeUpdate);
        Docteur testDocteur = docteurList.get(docteurList.size() - 1);
        assertThat(testDocteur.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingDocteur() throws Exception {
        int databaseSizeBeforeUpdate = docteurRepository.findAll().size();
        docteur.setId(count.incrementAndGet());

        // Create the Docteur
        DocteurDTO docteurDTO = docteurMapper.toDto(docteur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, docteurDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(docteurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDocteur() throws Exception {
        int databaseSizeBeforeUpdate = docteurRepository.findAll().size();
        docteur.setId(count.incrementAndGet());

        // Create the Docteur
        DocteurDTO docteurDTO = docteurMapper.toDto(docteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(docteurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDocteur() throws Exception {
        int databaseSizeBeforeUpdate = docteurRepository.findAll().size();
        docteur.setId(count.incrementAndGet());

        // Create the Docteur
        DocteurDTO docteurDTO = docteurMapper.toDto(docteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocteurMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(docteurDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Docteur in the database
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDocteur() throws Exception {
        // Initialize the database
        docteurRepository.saveAndFlush(docteur);

        int databaseSizeBeforeDelete = docteurRepository.findAll().size();

        // Delete the docteur
        restDocteurMockMvc
            .perform(delete(ENTITY_API_URL_ID, docteur.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Docteur> docteurList = docteurRepository.findAll();
        assertThat(docteurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
