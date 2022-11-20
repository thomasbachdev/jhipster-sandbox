package fr.uga.web.rest;

import static fr.uga.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.uga.IntegrationTest;
import fr.uga.domain.Docteur;
import fr.uga.domain.Rappel;
import fr.uga.repository.RappelRepository;
import fr.uga.service.criteria.RappelCriteria;
import fr.uga.service.dto.RappelDTO;
import fr.uga.service.mapper.RappelMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link RappelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RappelResourceIT {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String ENTITY_API_URL = "/api/rappels";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RappelRepository rappelRepository;

    @Autowired
    private RappelMapper rappelMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRappelMockMvc;

    private Rappel rappel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rappel createEntity(EntityManager em) {
        Rappel rappel = new Rappel().date(DEFAULT_DATE).description(DEFAULT_DESCRIPTION).deleted(DEFAULT_DELETED);
        return rappel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rappel createUpdatedEntity(EntityManager em) {
        Rappel rappel = new Rappel().date(UPDATED_DATE).description(UPDATED_DESCRIPTION).deleted(UPDATED_DELETED);
        return rappel;
    }

    @BeforeEach
    public void initTest() {
        rappel = createEntity(em);
    }

    @Test
    @Transactional
    void createRappel() throws Exception {
        int databaseSizeBeforeCreate = rappelRepository.findAll().size();
        // Create the Rappel
        RappelDTO rappelDTO = rappelMapper.toDto(rappel);
        restRappelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rappelDTO)))
            .andExpect(status().isCreated());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeCreate + 1);
        Rappel testRappel = rappelList.get(rappelList.size() - 1);
        assertThat(testRappel.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testRappel.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRappel.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void createRappelWithExistingId() throws Exception {
        // Create the Rappel with an existing ID
        rappel.setId(1L);
        RappelDTO rappelDTO = rappelMapper.toDto(rappel);

        int databaseSizeBeforeCreate = rappelRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRappelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rappelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = rappelRepository.findAll().size();
        // set the field null
        rappel.setDate(null);

        // Create the Rappel, which fails.
        RappelDTO rappelDTO = rappelMapper.toDto(rappel);

        restRappelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rappelDTO)))
            .andExpect(status().isBadRequest());

        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = rappelRepository.findAll().size();
        // set the field null
        rappel.setDescription(null);

        // Create the Rappel, which fails.
        RappelDTO rappelDTO = rappelMapper.toDto(rappel);

        restRappelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rappelDTO)))
            .andExpect(status().isBadRequest());

        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRappels() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList
        restRappelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rappel.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    void getRappel() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get the rappel
        restRappelMockMvc
            .perform(get(ENTITY_API_URL_ID, rappel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rappel.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getRappelsByIdFiltering() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        Long id = rappel.getId();

        defaultRappelShouldBeFound("id.equals=" + id);
        defaultRappelShouldNotBeFound("id.notEquals=" + id);

        defaultRappelShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRappelShouldNotBeFound("id.greaterThan=" + id);

        defaultRappelShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRappelShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRappelsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where date equals to DEFAULT_DATE
        defaultRappelShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the rappelList where date equals to UPDATED_DATE
        defaultRappelShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllRappelsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where date in DEFAULT_DATE or UPDATED_DATE
        defaultRappelShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the rappelList where date equals to UPDATED_DATE
        defaultRappelShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllRappelsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where date is not null
        defaultRappelShouldBeFound("date.specified=true");

        // Get all the rappelList where date is null
        defaultRappelShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllRappelsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where date is greater than or equal to DEFAULT_DATE
        defaultRappelShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the rappelList where date is greater than or equal to UPDATED_DATE
        defaultRappelShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllRappelsByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where date is less than or equal to DEFAULT_DATE
        defaultRappelShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the rappelList where date is less than or equal to SMALLER_DATE
        defaultRappelShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllRappelsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where date is less than DEFAULT_DATE
        defaultRappelShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the rappelList where date is less than UPDATED_DATE
        defaultRappelShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllRappelsByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where date is greater than DEFAULT_DATE
        defaultRappelShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the rappelList where date is greater than SMALLER_DATE
        defaultRappelShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllRappelsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where description equals to DEFAULT_DESCRIPTION
        defaultRappelShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the rappelList where description equals to UPDATED_DESCRIPTION
        defaultRappelShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRappelsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultRappelShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the rappelList where description equals to UPDATED_DESCRIPTION
        defaultRappelShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRappelsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where description is not null
        defaultRappelShouldBeFound("description.specified=true");

        // Get all the rappelList where description is null
        defaultRappelShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllRappelsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where description contains DEFAULT_DESCRIPTION
        defaultRappelShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the rappelList where description contains UPDATED_DESCRIPTION
        defaultRappelShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRappelsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where description does not contain DEFAULT_DESCRIPTION
        defaultRappelShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the rappelList where description does not contain UPDATED_DESCRIPTION
        defaultRappelShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRappelsByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where deleted equals to DEFAULT_DELETED
        defaultRappelShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the rappelList where deleted equals to UPDATED_DELETED
        defaultRappelShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllRappelsByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultRappelShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the rappelList where deleted equals to UPDATED_DELETED
        defaultRappelShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllRappelsByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        // Get all the rappelList where deleted is not null
        defaultRappelShouldBeFound("deleted.specified=true");

        // Get all the rappelList where deleted is null
        defaultRappelShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllRappelsByDocteurIsEqualToSomething() throws Exception {
        Docteur docteur;
        if (TestUtil.findAll(em, Docteur.class).isEmpty()) {
            rappelRepository.saveAndFlush(rappel);
            docteur = DocteurResourceIT.createEntity(em);
        } else {
            docteur = TestUtil.findAll(em, Docteur.class).get(0);
        }
        em.persist(docteur);
        em.flush();
        rappel.setDocteur(docteur);
        rappelRepository.saveAndFlush(rappel);
        Long docteurId = docteur.getId();

        // Get all the rappelList where docteur equals to docteurId
        defaultRappelShouldBeFound("docteurId.equals=" + docteurId);

        // Get all the rappelList where docteur equals to (docteurId + 1)
        defaultRappelShouldNotBeFound("docteurId.equals=" + (docteurId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRappelShouldBeFound(String filter) throws Exception {
        restRappelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rappel.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));

        // Check, that the count call also returns 1
        restRappelMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRappelShouldNotBeFound(String filter) throws Exception {
        restRappelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRappelMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRappel() throws Exception {
        // Get the rappel
        restRappelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRappel() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        int databaseSizeBeforeUpdate = rappelRepository.findAll().size();

        // Update the rappel
        Rappel updatedRappel = rappelRepository.findById(rappel.getId()).get();
        // Disconnect from session so that the updates on updatedRappel are not directly saved in db
        em.detach(updatedRappel);
        updatedRappel.date(UPDATED_DATE).description(UPDATED_DESCRIPTION).deleted(UPDATED_DELETED);
        RappelDTO rappelDTO = rappelMapper.toDto(updatedRappel);

        restRappelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rappelDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rappelDTO))
            )
            .andExpect(status().isOk());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeUpdate);
        Rappel testRappel = rappelList.get(rappelList.size() - 1);
        assertThat(testRappel.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testRappel.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRappel.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void putNonExistingRappel() throws Exception {
        int databaseSizeBeforeUpdate = rappelRepository.findAll().size();
        rappel.setId(count.incrementAndGet());

        // Create the Rappel
        RappelDTO rappelDTO = rappelMapper.toDto(rappel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRappelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rappelDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rappelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRappel() throws Exception {
        int databaseSizeBeforeUpdate = rappelRepository.findAll().size();
        rappel.setId(count.incrementAndGet());

        // Create the Rappel
        RappelDTO rappelDTO = rappelMapper.toDto(rappel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRappelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rappelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRappel() throws Exception {
        int databaseSizeBeforeUpdate = rappelRepository.findAll().size();
        rappel.setId(count.incrementAndGet());

        // Create the Rappel
        RappelDTO rappelDTO = rappelMapper.toDto(rappel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRappelMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rappelDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRappelWithPatch() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        int databaseSizeBeforeUpdate = rappelRepository.findAll().size();

        // Update the rappel using partial update
        Rappel partialUpdatedRappel = new Rappel();
        partialUpdatedRappel.setId(rappel.getId());

        partialUpdatedRappel.date(UPDATED_DATE).deleted(UPDATED_DELETED);

        restRappelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRappel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRappel))
            )
            .andExpect(status().isOk());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeUpdate);
        Rappel testRappel = rappelList.get(rappelList.size() - 1);
        assertThat(testRappel.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testRappel.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRappel.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void fullUpdateRappelWithPatch() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        int databaseSizeBeforeUpdate = rappelRepository.findAll().size();

        // Update the rappel using partial update
        Rappel partialUpdatedRappel = new Rappel();
        partialUpdatedRappel.setId(rappel.getId());

        partialUpdatedRappel.date(UPDATED_DATE).description(UPDATED_DESCRIPTION).deleted(UPDATED_DELETED);

        restRappelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRappel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRappel))
            )
            .andExpect(status().isOk());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeUpdate);
        Rappel testRappel = rappelList.get(rappelList.size() - 1);
        assertThat(testRappel.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testRappel.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRappel.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingRappel() throws Exception {
        int databaseSizeBeforeUpdate = rappelRepository.findAll().size();
        rappel.setId(count.incrementAndGet());

        // Create the Rappel
        RappelDTO rappelDTO = rappelMapper.toDto(rappel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRappelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rappelDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rappelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRappel() throws Exception {
        int databaseSizeBeforeUpdate = rappelRepository.findAll().size();
        rappel.setId(count.incrementAndGet());

        // Create the Rappel
        RappelDTO rappelDTO = rappelMapper.toDto(rappel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRappelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rappelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRappel() throws Exception {
        int databaseSizeBeforeUpdate = rappelRepository.findAll().size();
        rappel.setId(count.incrementAndGet());

        // Create the Rappel
        RappelDTO rappelDTO = rappelMapper.toDto(rappel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRappelMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(rappelDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rappel in the database
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRappel() throws Exception {
        // Initialize the database
        rappelRepository.saveAndFlush(rappel);

        int databaseSizeBeforeDelete = rappelRepository.findAll().size();

        // Delete the rappel
        restRappelMockMvc
            .perform(delete(ENTITY_API_URL_ID, rappel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Rappel> rappelList = rappelRepository.findAll();
        assertThat(rappelList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
