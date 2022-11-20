package fr.uga.web.rest;

import static fr.uga.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.uga.IntegrationTest;
import fr.uga.domain.Demande;
import fr.uga.domain.Docteur;
import fr.uga.domain.Resident;
import fr.uga.repository.DemandeRepository;
import fr.uga.service.criteria.DemandeCriteria;
import fr.uga.service.dto.DemandeDTO;
import fr.uga.service.mapper.DemandeMapper;
import java.time.Instant;
import java.time.LocalDate;
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
 * Integration tests for the {@link DemandeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DemandeResourceIT {

    private static final ZonedDateTime DEFAULT_DATE_EMITION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_EMITION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE_EMITION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final LocalDate DEFAULT_DATE_LIMITE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_LIMITE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_LIMITE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String ENTITY_API_URL = "/api/demandes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DemandeMapper demandeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDemandeMockMvc;

    private Demande demande;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demande createEntity(EntityManager em) {
        Demande demande = new Demande()
            .dateEmition(DEFAULT_DATE_EMITION)
            .dateLimite(DEFAULT_DATE_LIMITE)
            .description(DEFAULT_DESCRIPTION)
            .deleted(DEFAULT_DELETED);
        return demande;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demande createUpdatedEntity(EntityManager em) {
        Demande demande = new Demande()
            .dateEmition(UPDATED_DATE_EMITION)
            .dateLimite(UPDATED_DATE_LIMITE)
            .description(UPDATED_DESCRIPTION)
            .deleted(UPDATED_DELETED);
        return demande;
    }

    @BeforeEach
    public void initTest() {
        demande = createEntity(em);
    }

    @Test
    @Transactional
    void createDemande() throws Exception {
        int databaseSizeBeforeCreate = demandeRepository.findAll().size();
        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);
        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(demandeDTO)))
            .andExpect(status().isCreated());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeCreate + 1);
        Demande testDemande = demandeList.get(demandeList.size() - 1);
        assertThat(testDemande.getDateEmition()).isEqualTo(DEFAULT_DATE_EMITION);
        assertThat(testDemande.getDateLimite()).isEqualTo(DEFAULT_DATE_LIMITE);
        assertThat(testDemande.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testDemande.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void createDemandeWithExistingId() throws Exception {
        // Create the Demande with an existing ID
        demande.setId(1L);
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        int databaseSizeBeforeCreate = demandeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateEmitionIsRequired() throws Exception {
        int databaseSizeBeforeTest = demandeRepository.findAll().size();
        // set the field null
        demande.setDateEmition(null);

        // Create the Demande, which fails.
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateLimiteIsRequired() throws Exception {
        int databaseSizeBeforeTest = demandeRepository.findAll().size();
        // set the field null
        demande.setDateLimite(null);

        // Create the Demande, which fails.
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = demandeRepository.findAll().size();
        // set the field null
        demande.setDescription(null);

        // Create the Demande, which fails.
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDemandes() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demande.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateEmition").value(hasItem(sameInstant(DEFAULT_DATE_EMITION))))
            .andExpect(jsonPath("$.[*].dateLimite").value(hasItem(DEFAULT_DATE_LIMITE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    void getDemande() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get the demande
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL_ID, demande.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(demande.getId().intValue()))
            .andExpect(jsonPath("$.dateEmition").value(sameInstant(DEFAULT_DATE_EMITION)))
            .andExpect(jsonPath("$.dateLimite").value(DEFAULT_DATE_LIMITE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getDemandesByIdFiltering() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        Long id = demande.getId();

        defaultDemandeShouldBeFound("id.equals=" + id);
        defaultDemandeShouldNotBeFound("id.notEquals=" + id);

        defaultDemandeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDemandeShouldNotBeFound("id.greaterThan=" + id);

        defaultDemandeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDemandeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDemandesByDateEmitionIsEqualToSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateEmition equals to DEFAULT_DATE_EMITION
        defaultDemandeShouldBeFound("dateEmition.equals=" + DEFAULT_DATE_EMITION);

        // Get all the demandeList where dateEmition equals to UPDATED_DATE_EMITION
        defaultDemandeShouldNotBeFound("dateEmition.equals=" + UPDATED_DATE_EMITION);
    }

    @Test
    @Transactional
    void getAllDemandesByDateEmitionIsInShouldWork() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateEmition in DEFAULT_DATE_EMITION or UPDATED_DATE_EMITION
        defaultDemandeShouldBeFound("dateEmition.in=" + DEFAULT_DATE_EMITION + "," + UPDATED_DATE_EMITION);

        // Get all the demandeList where dateEmition equals to UPDATED_DATE_EMITION
        defaultDemandeShouldNotBeFound("dateEmition.in=" + UPDATED_DATE_EMITION);
    }

    @Test
    @Transactional
    void getAllDemandesByDateEmitionIsNullOrNotNull() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateEmition is not null
        defaultDemandeShouldBeFound("dateEmition.specified=true");

        // Get all the demandeList where dateEmition is null
        defaultDemandeShouldNotBeFound("dateEmition.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByDateEmitionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateEmition is greater than or equal to DEFAULT_DATE_EMITION
        defaultDemandeShouldBeFound("dateEmition.greaterThanOrEqual=" + DEFAULT_DATE_EMITION);

        // Get all the demandeList where dateEmition is greater than or equal to UPDATED_DATE_EMITION
        defaultDemandeShouldNotBeFound("dateEmition.greaterThanOrEqual=" + UPDATED_DATE_EMITION);
    }

    @Test
    @Transactional
    void getAllDemandesByDateEmitionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateEmition is less than or equal to DEFAULT_DATE_EMITION
        defaultDemandeShouldBeFound("dateEmition.lessThanOrEqual=" + DEFAULT_DATE_EMITION);

        // Get all the demandeList where dateEmition is less than or equal to SMALLER_DATE_EMITION
        defaultDemandeShouldNotBeFound("dateEmition.lessThanOrEqual=" + SMALLER_DATE_EMITION);
    }

    @Test
    @Transactional
    void getAllDemandesByDateEmitionIsLessThanSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateEmition is less than DEFAULT_DATE_EMITION
        defaultDemandeShouldNotBeFound("dateEmition.lessThan=" + DEFAULT_DATE_EMITION);

        // Get all the demandeList where dateEmition is less than UPDATED_DATE_EMITION
        defaultDemandeShouldBeFound("dateEmition.lessThan=" + UPDATED_DATE_EMITION);
    }

    @Test
    @Transactional
    void getAllDemandesByDateEmitionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateEmition is greater than DEFAULT_DATE_EMITION
        defaultDemandeShouldNotBeFound("dateEmition.greaterThan=" + DEFAULT_DATE_EMITION);

        // Get all the demandeList where dateEmition is greater than SMALLER_DATE_EMITION
        defaultDemandeShouldBeFound("dateEmition.greaterThan=" + SMALLER_DATE_EMITION);
    }

    @Test
    @Transactional
    void getAllDemandesByDateLimiteIsEqualToSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateLimite equals to DEFAULT_DATE_LIMITE
        defaultDemandeShouldBeFound("dateLimite.equals=" + DEFAULT_DATE_LIMITE);

        // Get all the demandeList where dateLimite equals to UPDATED_DATE_LIMITE
        defaultDemandeShouldNotBeFound("dateLimite.equals=" + UPDATED_DATE_LIMITE);
    }

    @Test
    @Transactional
    void getAllDemandesByDateLimiteIsInShouldWork() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateLimite in DEFAULT_DATE_LIMITE or UPDATED_DATE_LIMITE
        defaultDemandeShouldBeFound("dateLimite.in=" + DEFAULT_DATE_LIMITE + "," + UPDATED_DATE_LIMITE);

        // Get all the demandeList where dateLimite equals to UPDATED_DATE_LIMITE
        defaultDemandeShouldNotBeFound("dateLimite.in=" + UPDATED_DATE_LIMITE);
    }

    @Test
    @Transactional
    void getAllDemandesByDateLimiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateLimite is not null
        defaultDemandeShouldBeFound("dateLimite.specified=true");

        // Get all the demandeList where dateLimite is null
        defaultDemandeShouldNotBeFound("dateLimite.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByDateLimiteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateLimite is greater than or equal to DEFAULT_DATE_LIMITE
        defaultDemandeShouldBeFound("dateLimite.greaterThanOrEqual=" + DEFAULT_DATE_LIMITE);

        // Get all the demandeList where dateLimite is greater than or equal to UPDATED_DATE_LIMITE
        defaultDemandeShouldNotBeFound("dateLimite.greaterThanOrEqual=" + UPDATED_DATE_LIMITE);
    }

    @Test
    @Transactional
    void getAllDemandesByDateLimiteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateLimite is less than or equal to DEFAULT_DATE_LIMITE
        defaultDemandeShouldBeFound("dateLimite.lessThanOrEqual=" + DEFAULT_DATE_LIMITE);

        // Get all the demandeList where dateLimite is less than or equal to SMALLER_DATE_LIMITE
        defaultDemandeShouldNotBeFound("dateLimite.lessThanOrEqual=" + SMALLER_DATE_LIMITE);
    }

    @Test
    @Transactional
    void getAllDemandesByDateLimiteIsLessThanSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateLimite is less than DEFAULT_DATE_LIMITE
        defaultDemandeShouldNotBeFound("dateLimite.lessThan=" + DEFAULT_DATE_LIMITE);

        // Get all the demandeList where dateLimite is less than UPDATED_DATE_LIMITE
        defaultDemandeShouldBeFound("dateLimite.lessThan=" + UPDATED_DATE_LIMITE);
    }

    @Test
    @Transactional
    void getAllDemandesByDateLimiteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateLimite is greater than DEFAULT_DATE_LIMITE
        defaultDemandeShouldNotBeFound("dateLimite.greaterThan=" + DEFAULT_DATE_LIMITE);

        // Get all the demandeList where dateLimite is greater than SMALLER_DATE_LIMITE
        defaultDemandeShouldBeFound("dateLimite.greaterThan=" + SMALLER_DATE_LIMITE);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description equals to DEFAULT_DESCRIPTION
        defaultDemandeShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the demandeList where description equals to UPDATED_DESCRIPTION
        defaultDemandeShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultDemandeShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the demandeList where description equals to UPDATED_DESCRIPTION
        defaultDemandeShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description is not null
        defaultDemandeShouldBeFound("description.specified=true");

        // Get all the demandeList where description is null
        defaultDemandeShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description contains DEFAULT_DESCRIPTION
        defaultDemandeShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the demandeList where description contains UPDATED_DESCRIPTION
        defaultDemandeShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description does not contain DEFAULT_DESCRIPTION
        defaultDemandeShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the demandeList where description does not contain UPDATED_DESCRIPTION
        defaultDemandeShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where deleted equals to DEFAULT_DELETED
        defaultDemandeShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the demandeList where deleted equals to UPDATED_DELETED
        defaultDemandeShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllDemandesByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultDemandeShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the demandeList where deleted equals to UPDATED_DELETED
        defaultDemandeShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllDemandesByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where deleted is not null
        defaultDemandeShouldBeFound("deleted.specified=true");

        // Get all the demandeList where deleted is null
        defaultDemandeShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByResidentIsEqualToSomething() throws Exception {
        Resident resident;
        if (TestUtil.findAll(em, Resident.class).isEmpty()) {
            demandeRepository.saveAndFlush(demande);
            resident = ResidentResourceIT.createEntity(em);
        } else {
            resident = TestUtil.findAll(em, Resident.class).get(0);
        }
        em.persist(resident);
        em.flush();
        demande.setResident(resident);
        demandeRepository.saveAndFlush(demande);
        Long residentId = resident.getId();

        // Get all the demandeList where resident equals to residentId
        defaultDemandeShouldBeFound("residentId.equals=" + residentId);

        // Get all the demandeList where resident equals to (residentId + 1)
        defaultDemandeShouldNotBeFound("residentId.equals=" + (residentId + 1));
    }

    @Test
    @Transactional
    void getAllDemandesByDocteurIsEqualToSomething() throws Exception {
        Docteur docteur;
        if (TestUtil.findAll(em, Docteur.class).isEmpty()) {
            demandeRepository.saveAndFlush(demande);
            docteur = DocteurResourceIT.createEntity(em);
        } else {
            docteur = TestUtil.findAll(em, Docteur.class).get(0);
        }
        em.persist(docteur);
        em.flush();
        demande.setDocteur(docteur);
        demandeRepository.saveAndFlush(demande);
        Long docteurId = docteur.getId();

        // Get all the demandeList where docteur equals to docteurId
        defaultDemandeShouldBeFound("docteurId.equals=" + docteurId);

        // Get all the demandeList where docteur equals to (docteurId + 1)
        defaultDemandeShouldNotBeFound("docteurId.equals=" + (docteurId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDemandeShouldBeFound(String filter) throws Exception {
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demande.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateEmition").value(hasItem(sameInstant(DEFAULT_DATE_EMITION))))
            .andExpect(jsonPath("$.[*].dateLimite").value(hasItem(DEFAULT_DATE_LIMITE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));

        // Check, that the count call also returns 1
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDemandeShouldNotBeFound(String filter) throws Exception {
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDemande() throws Exception {
        // Get the demande
        restDemandeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDemande() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();

        // Update the demande
        Demande updatedDemande = demandeRepository.findById(demande.getId()).get();
        // Disconnect from session so that the updates on updatedDemande are not directly saved in db
        em.detach(updatedDemande);
        updatedDemande
            .dateEmition(UPDATED_DATE_EMITION)
            .dateLimite(UPDATED_DATE_LIMITE)
            .description(UPDATED_DESCRIPTION)
            .deleted(UPDATED_DELETED);
        DemandeDTO demandeDTO = demandeMapper.toDto(updatedDemande);

        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
        Demande testDemande = demandeList.get(demandeList.size() - 1);
        assertThat(testDemande.getDateEmition()).isEqualTo(UPDATED_DATE_EMITION);
        assertThat(testDemande.getDateLimite()).isEqualTo(UPDATED_DATE_LIMITE);
        assertThat(testDemande.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDemande.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void putNonExistingDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(demandeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDemandeWithPatch() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();

        // Update the demande using partial update
        Demande partialUpdatedDemande = new Demande();
        partialUpdatedDemande.setId(demande.getId());

        partialUpdatedDemande
            .dateEmition(UPDATED_DATE_EMITION)
            .dateLimite(UPDATED_DATE_LIMITE)
            .description(UPDATED_DESCRIPTION)
            .deleted(UPDATED_DELETED);

        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemande.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDemande))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
        Demande testDemande = demandeList.get(demandeList.size() - 1);
        assertThat(testDemande.getDateEmition()).isEqualTo(UPDATED_DATE_EMITION);
        assertThat(testDemande.getDateLimite()).isEqualTo(UPDATED_DATE_LIMITE);
        assertThat(testDemande.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDemande.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void fullUpdateDemandeWithPatch() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();

        // Update the demande using partial update
        Demande partialUpdatedDemande = new Demande();
        partialUpdatedDemande.setId(demande.getId());

        partialUpdatedDemande
            .dateEmition(UPDATED_DATE_EMITION)
            .dateLimite(UPDATED_DATE_LIMITE)
            .description(UPDATED_DESCRIPTION)
            .deleted(UPDATED_DELETED);

        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemande.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDemande))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
        Demande testDemande = demandeList.get(demandeList.size() - 1);
        assertThat(testDemande.getDateEmition()).isEqualTo(UPDATED_DATE_EMITION);
        assertThat(testDemande.getDateLimite()).isEqualTo(UPDATED_DATE_LIMITE);
        assertThat(testDemande.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDemande.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, demandeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDemande() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        int databaseSizeBeforeDelete = demandeRepository.findAll().size();

        // Delete the demande
        restDemandeMockMvc
            .perform(delete(ENTITY_API_URL_ID, demande.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
