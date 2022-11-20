package fr.uga.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.uga.IntegrationTest;
import fr.uga.domain.Demande;
import fr.uga.domain.Etablissement;
import fr.uga.domain.Examen;
import fr.uga.domain.Resident;
import fr.uga.domain.enumeration.Sexe;
import fr.uga.domain.enumeration.StadeDenutrition;
import fr.uga.repository.ResidentRepository;
import fr.uga.service.criteria.ResidentCriteria;
import fr.uga.service.dto.ResidentDTO;
import fr.uga.service.mapper.ResidentMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ResidentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ResidentResourceIT {

    private static final Integer DEFAULT_NUMERO = 1;
    private static final Integer UPDATED_NUMERO = 2;
    private static final Integer SMALLER_NUMERO = 1 - 1;

    private static final String DEFAULT_NOM = "Dgc";
    private static final String UPDATED_NOM = "Nqws";

    private static final String DEFAULT_PRENOM = "Rjl";
    private static final String UPDATED_PRENOM = "Pphutg";

    private static final LocalDate DEFAULT_DATE_NAISSANCE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_NAISSANCE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_NAISSANCE = LocalDate.ofEpochDay(-1L);

    private static final Sexe DEFAULT_SEXE = Sexe.FEMININ;
    private static final Sexe UPDATED_SEXE = Sexe.MASCULIN;

    private static final LocalDate DEFAULT_DATE_ARRIVEE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ARRIVEE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_ARRIVEE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_CHAMBRE = "AAAAAA";
    private static final String UPDATED_CHAMBRE = "BBBBBB";

    private static final Float DEFAULT_TAILLE = 1F;
    private static final Float UPDATED_TAILLE = 2F;
    private static final Float SMALLER_TAILLE = 1F - 1F;

    private static final StadeDenutrition DEFAULT_DENUTRITION = StadeDenutrition.NON_DENUTRI;
    private static final StadeDenutrition UPDATED_DENUTRITION = StadeDenutrition.MODEREE;

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String ENTITY_API_URL = "/api/residents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private ResidentMapper residentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResidentMockMvc;

    private Resident resident;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resident createEntity(EntityManager em) {
        Resident resident = new Resident()
            .numero(DEFAULT_NUMERO)
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .dateNaissance(DEFAULT_DATE_NAISSANCE)
            .sexe(DEFAULT_SEXE)
            .dateArrivee(DEFAULT_DATE_ARRIVEE)
            .chambre(DEFAULT_CHAMBRE)
            .taille(DEFAULT_TAILLE)
            .denutrition(DEFAULT_DENUTRITION)
            .deleted(DEFAULT_DELETED);
        return resident;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resident createUpdatedEntity(EntityManager em) {
        Resident resident = new Resident()
            .numero(UPDATED_NUMERO)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .sexe(UPDATED_SEXE)
            .dateArrivee(UPDATED_DATE_ARRIVEE)
            .chambre(UPDATED_CHAMBRE)
            .taille(UPDATED_TAILLE)
            .denutrition(UPDATED_DENUTRITION)
            .deleted(UPDATED_DELETED);
        return resident;
    }

    @BeforeEach
    public void initTest() {
        resident = createEntity(em);
    }

    @Test
    @Transactional
    void createResident() throws Exception {
        int databaseSizeBeforeCreate = residentRepository.findAll().size();
        // Create the Resident
        ResidentDTO residentDTO = residentMapper.toDto(resident);
        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isCreated());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeCreate + 1);
        Resident testResident = residentList.get(residentList.size() - 1);
        assertThat(testResident.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testResident.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testResident.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testResident.getDateNaissance()).isEqualTo(DEFAULT_DATE_NAISSANCE);
        assertThat(testResident.getSexe()).isEqualTo(DEFAULT_SEXE);
        assertThat(testResident.getDateArrivee()).isEqualTo(DEFAULT_DATE_ARRIVEE);
        assertThat(testResident.getChambre()).isEqualTo(DEFAULT_CHAMBRE);
        assertThat(testResident.getTaille()).isEqualTo(DEFAULT_TAILLE);
        assertThat(testResident.getDenutrition()).isEqualTo(DEFAULT_DENUTRITION);
        assertThat(testResident.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void createResidentWithExistingId() throws Exception {
        // Create the Resident with an existing ID
        resident.setId(1L);
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        int databaseSizeBeforeCreate = residentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumeroIsRequired() throws Exception {
        int databaseSizeBeforeTest = residentRepository.findAll().size();
        // set the field null
        resident.setNumero(null);

        // Create the Resident, which fails.
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isBadRequest());

        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = residentRepository.findAll().size();
        // set the field null
        resident.setNom(null);

        // Create the Resident, which fails.
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isBadRequest());

        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrenomIsRequired() throws Exception {
        int databaseSizeBeforeTest = residentRepository.findAll().size();
        // set the field null
        resident.setPrenom(null);

        // Create the Resident, which fails.
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isBadRequest());

        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateNaissanceIsRequired() throws Exception {
        int databaseSizeBeforeTest = residentRepository.findAll().size();
        // set the field null
        resident.setDateNaissance(null);

        // Create the Resident, which fails.
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isBadRequest());

        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSexeIsRequired() throws Exception {
        int databaseSizeBeforeTest = residentRepository.findAll().size();
        // set the field null
        resident.setSexe(null);

        // Create the Resident, which fails.
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isBadRequest());

        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateArriveeIsRequired() throws Exception {
        int databaseSizeBeforeTest = residentRepository.findAll().size();
        // set the field null
        resident.setDateArrivee(null);

        // Create the Resident, which fails.
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isBadRequest());

        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChambreIsRequired() throws Exception {
        int databaseSizeBeforeTest = residentRepository.findAll().size();
        // set the field null
        resident.setChambre(null);

        // Create the Resident, which fails.
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isBadRequest());

        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTailleIsRequired() throws Exception {
        int databaseSizeBeforeTest = residentRepository.findAll().size();
        // set the field null
        resident.setTaille(null);

        // Create the Resident, which fails.
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        restResidentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isBadRequest());

        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllResidents() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList
        restResidentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resident.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE.toString())))
            .andExpect(jsonPath("$.[*].dateArrivee").value(hasItem(DEFAULT_DATE_ARRIVEE.toString())))
            .andExpect(jsonPath("$.[*].chambre").value(hasItem(DEFAULT_CHAMBRE)))
            .andExpect(jsonPath("$.[*].taille").value(hasItem(DEFAULT_TAILLE.doubleValue())))
            .andExpect(jsonPath("$.[*].denutrition").value(hasItem(DEFAULT_DENUTRITION.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    void getResident() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get the resident
        restResidentMockMvc
            .perform(get(ENTITY_API_URL_ID, resident.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(resident.getId().intValue()))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.dateNaissance").value(DEFAULT_DATE_NAISSANCE.toString()))
            .andExpect(jsonPath("$.sexe").value(DEFAULT_SEXE.toString()))
            .andExpect(jsonPath("$.dateArrivee").value(DEFAULT_DATE_ARRIVEE.toString()))
            .andExpect(jsonPath("$.chambre").value(DEFAULT_CHAMBRE))
            .andExpect(jsonPath("$.taille").value(DEFAULT_TAILLE.doubleValue()))
            .andExpect(jsonPath("$.denutrition").value(DEFAULT_DENUTRITION.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getResidentsByIdFiltering() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        Long id = resident.getId();

        defaultResidentShouldBeFound("id.equals=" + id);
        defaultResidentShouldNotBeFound("id.notEquals=" + id);

        defaultResidentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultResidentShouldNotBeFound("id.greaterThan=" + id);

        defaultResidentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultResidentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllResidentsByNumeroIsEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where numero equals to DEFAULT_NUMERO
        defaultResidentShouldBeFound("numero.equals=" + DEFAULT_NUMERO);

        // Get all the residentList where numero equals to UPDATED_NUMERO
        defaultResidentShouldNotBeFound("numero.equals=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllResidentsByNumeroIsInShouldWork() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where numero in DEFAULT_NUMERO or UPDATED_NUMERO
        defaultResidentShouldBeFound("numero.in=" + DEFAULT_NUMERO + "," + UPDATED_NUMERO);

        // Get all the residentList where numero equals to UPDATED_NUMERO
        defaultResidentShouldNotBeFound("numero.in=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllResidentsByNumeroIsNullOrNotNull() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where numero is not null
        defaultResidentShouldBeFound("numero.specified=true");

        // Get all the residentList where numero is null
        defaultResidentShouldNotBeFound("numero.specified=false");
    }

    @Test
    @Transactional
    void getAllResidentsByNumeroIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where numero is greater than or equal to DEFAULT_NUMERO
        defaultResidentShouldBeFound("numero.greaterThanOrEqual=" + DEFAULT_NUMERO);

        // Get all the residentList where numero is greater than or equal to UPDATED_NUMERO
        defaultResidentShouldNotBeFound("numero.greaterThanOrEqual=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllResidentsByNumeroIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where numero is less than or equal to DEFAULT_NUMERO
        defaultResidentShouldBeFound("numero.lessThanOrEqual=" + DEFAULT_NUMERO);

        // Get all the residentList where numero is less than or equal to SMALLER_NUMERO
        defaultResidentShouldNotBeFound("numero.lessThanOrEqual=" + SMALLER_NUMERO);
    }

    @Test
    @Transactional
    void getAllResidentsByNumeroIsLessThanSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where numero is less than DEFAULT_NUMERO
        defaultResidentShouldNotBeFound("numero.lessThan=" + DEFAULT_NUMERO);

        // Get all the residentList where numero is less than UPDATED_NUMERO
        defaultResidentShouldBeFound("numero.lessThan=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllResidentsByNumeroIsGreaterThanSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where numero is greater than DEFAULT_NUMERO
        defaultResidentShouldNotBeFound("numero.greaterThan=" + DEFAULT_NUMERO);

        // Get all the residentList where numero is greater than SMALLER_NUMERO
        defaultResidentShouldBeFound("numero.greaterThan=" + SMALLER_NUMERO);
    }

    @Test
    @Transactional
    void getAllResidentsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where nom equals to DEFAULT_NOM
        defaultResidentShouldBeFound("nom.equals=" + DEFAULT_NOM);

        // Get all the residentList where nom equals to UPDATED_NOM
        defaultResidentShouldNotBeFound("nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllResidentsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where nom in DEFAULT_NOM or UPDATED_NOM
        defaultResidentShouldBeFound("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM);

        // Get all the residentList where nom equals to UPDATED_NOM
        defaultResidentShouldNotBeFound("nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllResidentsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where nom is not null
        defaultResidentShouldBeFound("nom.specified=true");

        // Get all the residentList where nom is null
        defaultResidentShouldNotBeFound("nom.specified=false");
    }

    @Test
    @Transactional
    void getAllResidentsByNomContainsSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where nom contains DEFAULT_NOM
        defaultResidentShouldBeFound("nom.contains=" + DEFAULT_NOM);

        // Get all the residentList where nom contains UPDATED_NOM
        defaultResidentShouldNotBeFound("nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllResidentsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where nom does not contain DEFAULT_NOM
        defaultResidentShouldNotBeFound("nom.doesNotContain=" + DEFAULT_NOM);

        // Get all the residentList where nom does not contain UPDATED_NOM
        defaultResidentShouldBeFound("nom.doesNotContain=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllResidentsByPrenomIsEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where prenom equals to DEFAULT_PRENOM
        defaultResidentShouldBeFound("prenom.equals=" + DEFAULT_PRENOM);

        // Get all the residentList where prenom equals to UPDATED_PRENOM
        defaultResidentShouldNotBeFound("prenom.equals=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllResidentsByPrenomIsInShouldWork() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where prenom in DEFAULT_PRENOM or UPDATED_PRENOM
        defaultResidentShouldBeFound("prenom.in=" + DEFAULT_PRENOM + "," + UPDATED_PRENOM);

        // Get all the residentList where prenom equals to UPDATED_PRENOM
        defaultResidentShouldNotBeFound("prenom.in=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllResidentsByPrenomIsNullOrNotNull() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where prenom is not null
        defaultResidentShouldBeFound("prenom.specified=true");

        // Get all the residentList where prenom is null
        defaultResidentShouldNotBeFound("prenom.specified=false");
    }

    @Test
    @Transactional
    void getAllResidentsByPrenomContainsSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where prenom contains DEFAULT_PRENOM
        defaultResidentShouldBeFound("prenom.contains=" + DEFAULT_PRENOM);

        // Get all the residentList where prenom contains UPDATED_PRENOM
        defaultResidentShouldNotBeFound("prenom.contains=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllResidentsByPrenomNotContainsSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where prenom does not contain DEFAULT_PRENOM
        defaultResidentShouldNotBeFound("prenom.doesNotContain=" + DEFAULT_PRENOM);

        // Get all the residentList where prenom does not contain UPDATED_PRENOM
        defaultResidentShouldBeFound("prenom.doesNotContain=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllResidentsByDateNaissanceIsEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateNaissance equals to DEFAULT_DATE_NAISSANCE
        defaultResidentShouldBeFound("dateNaissance.equals=" + DEFAULT_DATE_NAISSANCE);

        // Get all the residentList where dateNaissance equals to UPDATED_DATE_NAISSANCE
        defaultResidentShouldNotBeFound("dateNaissance.equals=" + UPDATED_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllResidentsByDateNaissanceIsInShouldWork() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateNaissance in DEFAULT_DATE_NAISSANCE or UPDATED_DATE_NAISSANCE
        defaultResidentShouldBeFound("dateNaissance.in=" + DEFAULT_DATE_NAISSANCE + "," + UPDATED_DATE_NAISSANCE);

        // Get all the residentList where dateNaissance equals to UPDATED_DATE_NAISSANCE
        defaultResidentShouldNotBeFound("dateNaissance.in=" + UPDATED_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllResidentsByDateNaissanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateNaissance is not null
        defaultResidentShouldBeFound("dateNaissance.specified=true");

        // Get all the residentList where dateNaissance is null
        defaultResidentShouldNotBeFound("dateNaissance.specified=false");
    }

    @Test
    @Transactional
    void getAllResidentsByDateNaissanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateNaissance is greater than or equal to DEFAULT_DATE_NAISSANCE
        defaultResidentShouldBeFound("dateNaissance.greaterThanOrEqual=" + DEFAULT_DATE_NAISSANCE);

        // Get all the residentList where dateNaissance is greater than or equal to UPDATED_DATE_NAISSANCE
        defaultResidentShouldNotBeFound("dateNaissance.greaterThanOrEqual=" + UPDATED_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllResidentsByDateNaissanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateNaissance is less than or equal to DEFAULT_DATE_NAISSANCE
        defaultResidentShouldBeFound("dateNaissance.lessThanOrEqual=" + DEFAULT_DATE_NAISSANCE);

        // Get all the residentList where dateNaissance is less than or equal to SMALLER_DATE_NAISSANCE
        defaultResidentShouldNotBeFound("dateNaissance.lessThanOrEqual=" + SMALLER_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllResidentsByDateNaissanceIsLessThanSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateNaissance is less than DEFAULT_DATE_NAISSANCE
        defaultResidentShouldNotBeFound("dateNaissance.lessThan=" + DEFAULT_DATE_NAISSANCE);

        // Get all the residentList where dateNaissance is less than UPDATED_DATE_NAISSANCE
        defaultResidentShouldBeFound("dateNaissance.lessThan=" + UPDATED_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllResidentsByDateNaissanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateNaissance is greater than DEFAULT_DATE_NAISSANCE
        defaultResidentShouldNotBeFound("dateNaissance.greaterThan=" + DEFAULT_DATE_NAISSANCE);

        // Get all the residentList where dateNaissance is greater than SMALLER_DATE_NAISSANCE
        defaultResidentShouldBeFound("dateNaissance.greaterThan=" + SMALLER_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllResidentsBySexeIsEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where sexe equals to DEFAULT_SEXE
        defaultResidentShouldBeFound("sexe.equals=" + DEFAULT_SEXE);

        // Get all the residentList where sexe equals to UPDATED_SEXE
        defaultResidentShouldNotBeFound("sexe.equals=" + UPDATED_SEXE);
    }

    @Test
    @Transactional
    void getAllResidentsBySexeIsInShouldWork() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where sexe in DEFAULT_SEXE or UPDATED_SEXE
        defaultResidentShouldBeFound("sexe.in=" + DEFAULT_SEXE + "," + UPDATED_SEXE);

        // Get all the residentList where sexe equals to UPDATED_SEXE
        defaultResidentShouldNotBeFound("sexe.in=" + UPDATED_SEXE);
    }

    @Test
    @Transactional
    void getAllResidentsBySexeIsNullOrNotNull() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where sexe is not null
        defaultResidentShouldBeFound("sexe.specified=true");

        // Get all the residentList where sexe is null
        defaultResidentShouldNotBeFound("sexe.specified=false");
    }

    @Test
    @Transactional
    void getAllResidentsByDateArriveeIsEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateArrivee equals to DEFAULT_DATE_ARRIVEE
        defaultResidentShouldBeFound("dateArrivee.equals=" + DEFAULT_DATE_ARRIVEE);

        // Get all the residentList where dateArrivee equals to UPDATED_DATE_ARRIVEE
        defaultResidentShouldNotBeFound("dateArrivee.equals=" + UPDATED_DATE_ARRIVEE);
    }

    @Test
    @Transactional
    void getAllResidentsByDateArriveeIsInShouldWork() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateArrivee in DEFAULT_DATE_ARRIVEE or UPDATED_DATE_ARRIVEE
        defaultResidentShouldBeFound("dateArrivee.in=" + DEFAULT_DATE_ARRIVEE + "," + UPDATED_DATE_ARRIVEE);

        // Get all the residentList where dateArrivee equals to UPDATED_DATE_ARRIVEE
        defaultResidentShouldNotBeFound("dateArrivee.in=" + UPDATED_DATE_ARRIVEE);
    }

    @Test
    @Transactional
    void getAllResidentsByDateArriveeIsNullOrNotNull() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateArrivee is not null
        defaultResidentShouldBeFound("dateArrivee.specified=true");

        // Get all the residentList where dateArrivee is null
        defaultResidentShouldNotBeFound("dateArrivee.specified=false");
    }

    @Test
    @Transactional
    void getAllResidentsByDateArriveeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateArrivee is greater than or equal to DEFAULT_DATE_ARRIVEE
        defaultResidentShouldBeFound("dateArrivee.greaterThanOrEqual=" + DEFAULT_DATE_ARRIVEE);

        // Get all the residentList where dateArrivee is greater than or equal to UPDATED_DATE_ARRIVEE
        defaultResidentShouldNotBeFound("dateArrivee.greaterThanOrEqual=" + UPDATED_DATE_ARRIVEE);
    }

    @Test
    @Transactional
    void getAllResidentsByDateArriveeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateArrivee is less than or equal to DEFAULT_DATE_ARRIVEE
        defaultResidentShouldBeFound("dateArrivee.lessThanOrEqual=" + DEFAULT_DATE_ARRIVEE);

        // Get all the residentList where dateArrivee is less than or equal to SMALLER_DATE_ARRIVEE
        defaultResidentShouldNotBeFound("dateArrivee.lessThanOrEqual=" + SMALLER_DATE_ARRIVEE);
    }

    @Test
    @Transactional
    void getAllResidentsByDateArriveeIsLessThanSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateArrivee is less than DEFAULT_DATE_ARRIVEE
        defaultResidentShouldNotBeFound("dateArrivee.lessThan=" + DEFAULT_DATE_ARRIVEE);

        // Get all the residentList where dateArrivee is less than UPDATED_DATE_ARRIVEE
        defaultResidentShouldBeFound("dateArrivee.lessThan=" + UPDATED_DATE_ARRIVEE);
    }

    @Test
    @Transactional
    void getAllResidentsByDateArriveeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where dateArrivee is greater than DEFAULT_DATE_ARRIVEE
        defaultResidentShouldNotBeFound("dateArrivee.greaterThan=" + DEFAULT_DATE_ARRIVEE);

        // Get all the residentList where dateArrivee is greater than SMALLER_DATE_ARRIVEE
        defaultResidentShouldBeFound("dateArrivee.greaterThan=" + SMALLER_DATE_ARRIVEE);
    }

    @Test
    @Transactional
    void getAllResidentsByChambreIsEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where chambre equals to DEFAULT_CHAMBRE
        defaultResidentShouldBeFound("chambre.equals=" + DEFAULT_CHAMBRE);

        // Get all the residentList where chambre equals to UPDATED_CHAMBRE
        defaultResidentShouldNotBeFound("chambre.equals=" + UPDATED_CHAMBRE);
    }

    @Test
    @Transactional
    void getAllResidentsByChambreIsInShouldWork() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where chambre in DEFAULT_CHAMBRE or UPDATED_CHAMBRE
        defaultResidentShouldBeFound("chambre.in=" + DEFAULT_CHAMBRE + "," + UPDATED_CHAMBRE);

        // Get all the residentList where chambre equals to UPDATED_CHAMBRE
        defaultResidentShouldNotBeFound("chambre.in=" + UPDATED_CHAMBRE);
    }

    @Test
    @Transactional
    void getAllResidentsByChambreIsNullOrNotNull() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where chambre is not null
        defaultResidentShouldBeFound("chambre.specified=true");

        // Get all the residentList where chambre is null
        defaultResidentShouldNotBeFound("chambre.specified=false");
    }

    @Test
    @Transactional
    void getAllResidentsByChambreContainsSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where chambre contains DEFAULT_CHAMBRE
        defaultResidentShouldBeFound("chambre.contains=" + DEFAULT_CHAMBRE);

        // Get all the residentList where chambre contains UPDATED_CHAMBRE
        defaultResidentShouldNotBeFound("chambre.contains=" + UPDATED_CHAMBRE);
    }

    @Test
    @Transactional
    void getAllResidentsByChambreNotContainsSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where chambre does not contain DEFAULT_CHAMBRE
        defaultResidentShouldNotBeFound("chambre.doesNotContain=" + DEFAULT_CHAMBRE);

        // Get all the residentList where chambre does not contain UPDATED_CHAMBRE
        defaultResidentShouldBeFound("chambre.doesNotContain=" + UPDATED_CHAMBRE);
    }

    @Test
    @Transactional
    void getAllResidentsByTailleIsEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where taille equals to DEFAULT_TAILLE
        defaultResidentShouldBeFound("taille.equals=" + DEFAULT_TAILLE);

        // Get all the residentList where taille equals to UPDATED_TAILLE
        defaultResidentShouldNotBeFound("taille.equals=" + UPDATED_TAILLE);
    }

    @Test
    @Transactional
    void getAllResidentsByTailleIsInShouldWork() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where taille in DEFAULT_TAILLE or UPDATED_TAILLE
        defaultResidentShouldBeFound("taille.in=" + DEFAULT_TAILLE + "," + UPDATED_TAILLE);

        // Get all the residentList where taille equals to UPDATED_TAILLE
        defaultResidentShouldNotBeFound("taille.in=" + UPDATED_TAILLE);
    }

    @Test
    @Transactional
    void getAllResidentsByTailleIsNullOrNotNull() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where taille is not null
        defaultResidentShouldBeFound("taille.specified=true");

        // Get all the residentList where taille is null
        defaultResidentShouldNotBeFound("taille.specified=false");
    }

    @Test
    @Transactional
    void getAllResidentsByTailleIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where taille is greater than or equal to DEFAULT_TAILLE
        defaultResidentShouldBeFound("taille.greaterThanOrEqual=" + DEFAULT_TAILLE);

        // Get all the residentList where taille is greater than or equal to UPDATED_TAILLE
        defaultResidentShouldNotBeFound("taille.greaterThanOrEqual=" + UPDATED_TAILLE);
    }

    @Test
    @Transactional
    void getAllResidentsByTailleIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where taille is less than or equal to DEFAULT_TAILLE
        defaultResidentShouldBeFound("taille.lessThanOrEqual=" + DEFAULT_TAILLE);

        // Get all the residentList where taille is less than or equal to SMALLER_TAILLE
        defaultResidentShouldNotBeFound("taille.lessThanOrEqual=" + SMALLER_TAILLE);
    }

    @Test
    @Transactional
    void getAllResidentsByTailleIsLessThanSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where taille is less than DEFAULT_TAILLE
        defaultResidentShouldNotBeFound("taille.lessThan=" + DEFAULT_TAILLE);

        // Get all the residentList where taille is less than UPDATED_TAILLE
        defaultResidentShouldBeFound("taille.lessThan=" + UPDATED_TAILLE);
    }

    @Test
    @Transactional
    void getAllResidentsByTailleIsGreaterThanSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where taille is greater than DEFAULT_TAILLE
        defaultResidentShouldNotBeFound("taille.greaterThan=" + DEFAULT_TAILLE);

        // Get all the residentList where taille is greater than SMALLER_TAILLE
        defaultResidentShouldBeFound("taille.greaterThan=" + SMALLER_TAILLE);
    }

    @Test
    @Transactional
    void getAllResidentsByDenutritionIsEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where denutrition equals to DEFAULT_DENUTRITION
        defaultResidentShouldBeFound("denutrition.equals=" + DEFAULT_DENUTRITION);

        // Get all the residentList where denutrition equals to UPDATED_DENUTRITION
        defaultResidentShouldNotBeFound("denutrition.equals=" + UPDATED_DENUTRITION);
    }

    @Test
    @Transactional
    void getAllResidentsByDenutritionIsInShouldWork() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where denutrition in DEFAULT_DENUTRITION or UPDATED_DENUTRITION
        defaultResidentShouldBeFound("denutrition.in=" + DEFAULT_DENUTRITION + "," + UPDATED_DENUTRITION);

        // Get all the residentList where denutrition equals to UPDATED_DENUTRITION
        defaultResidentShouldNotBeFound("denutrition.in=" + UPDATED_DENUTRITION);
    }

    @Test
    @Transactional
    void getAllResidentsByDenutritionIsNullOrNotNull() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where denutrition is not null
        defaultResidentShouldBeFound("denutrition.specified=true");

        // Get all the residentList where denutrition is null
        defaultResidentShouldNotBeFound("denutrition.specified=false");
    }

    @Test
    @Transactional
    void getAllResidentsByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where deleted equals to DEFAULT_DELETED
        defaultResidentShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the residentList where deleted equals to UPDATED_DELETED
        defaultResidentShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllResidentsByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultResidentShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the residentList where deleted equals to UPDATED_DELETED
        defaultResidentShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllResidentsByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        // Get all the residentList where deleted is not null
        defaultResidentShouldBeFound("deleted.specified=true");

        // Get all the residentList where deleted is null
        defaultResidentShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllResidentsByEtablissementIsEqualToSomething() throws Exception {
        Etablissement etablissement;
        if (TestUtil.findAll(em, Etablissement.class).isEmpty()) {
            residentRepository.saveAndFlush(resident);
            etablissement = EtablissementResourceIT.createEntity(em);
        } else {
            etablissement = TestUtil.findAll(em, Etablissement.class).get(0);
        }
        em.persist(etablissement);
        em.flush();
        resident.setEtablissement(etablissement);
        residentRepository.saveAndFlush(resident);
        Long etablissementId = etablissement.getId();

        // Get all the residentList where etablissement equals to etablissementId
        defaultResidentShouldBeFound("etablissementId.equals=" + etablissementId);

        // Get all the residentList where etablissement equals to (etablissementId + 1)
        defaultResidentShouldNotBeFound("etablissementId.equals=" + (etablissementId + 1));
    }

    @Test
    @Transactional
    void getAllResidentsByDemandeIsEqualToSomething() throws Exception {
        Demande demande;
        if (TestUtil.findAll(em, Demande.class).isEmpty()) {
            residentRepository.saveAndFlush(resident);
            demande = DemandeResourceIT.createEntity(em);
        } else {
            demande = TestUtil.findAll(em, Demande.class).get(0);
        }
        em.persist(demande);
        em.flush();
        resident.addDemande(demande);
        residentRepository.saveAndFlush(resident);
        Long demandeId = demande.getId();

        // Get all the residentList where demande equals to demandeId
        defaultResidentShouldBeFound("demandeId.equals=" + demandeId);

        // Get all the residentList where demande equals to (demandeId + 1)
        defaultResidentShouldNotBeFound("demandeId.equals=" + (demandeId + 1));
    }

    @Test
    @Transactional
    void getAllResidentsByExamenIsEqualToSomething() throws Exception {
        Examen examen;
        if (TestUtil.findAll(em, Examen.class).isEmpty()) {
            residentRepository.saveAndFlush(resident);
            examen = ExamenResourceIT.createEntity(em);
        } else {
            examen = TestUtil.findAll(em, Examen.class).get(0);
        }
        em.persist(examen);
        em.flush();
        resident.addExamen(examen);
        residentRepository.saveAndFlush(resident);
        Long examenId = examen.getId();

        // Get all the residentList where examen equals to examenId
        defaultResidentShouldBeFound("examenId.equals=" + examenId);

        // Get all the residentList where examen equals to (examenId + 1)
        defaultResidentShouldNotBeFound("examenId.equals=" + (examenId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultResidentShouldBeFound(String filter) throws Exception {
        restResidentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resident.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE.toString())))
            .andExpect(jsonPath("$.[*].dateArrivee").value(hasItem(DEFAULT_DATE_ARRIVEE.toString())))
            .andExpect(jsonPath("$.[*].chambre").value(hasItem(DEFAULT_CHAMBRE)))
            .andExpect(jsonPath("$.[*].taille").value(hasItem(DEFAULT_TAILLE.doubleValue())))
            .andExpect(jsonPath("$.[*].denutrition").value(hasItem(DEFAULT_DENUTRITION.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));

        // Check, that the count call also returns 1
        restResidentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultResidentShouldNotBeFound(String filter) throws Exception {
        restResidentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restResidentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingResident() throws Exception {
        // Get the resident
        restResidentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingResident() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        int databaseSizeBeforeUpdate = residentRepository.findAll().size();

        // Update the resident
        Resident updatedResident = residentRepository.findById(resident.getId()).get();
        // Disconnect from session so that the updates on updatedResident are not directly saved in db
        em.detach(updatedResident);
        updatedResident
            .numero(UPDATED_NUMERO)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .sexe(UPDATED_SEXE)
            .dateArrivee(UPDATED_DATE_ARRIVEE)
            .chambre(UPDATED_CHAMBRE)
            .taille(UPDATED_TAILLE)
            .denutrition(UPDATED_DENUTRITION)
            .deleted(UPDATED_DELETED);
        ResidentDTO residentDTO = residentMapper.toDto(updatedResident);

        restResidentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, residentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(residentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
        Resident testResident = residentList.get(residentList.size() - 1);
        assertThat(testResident.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testResident.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testResident.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testResident.getDateNaissance()).isEqualTo(UPDATED_DATE_NAISSANCE);
        assertThat(testResident.getSexe()).isEqualTo(UPDATED_SEXE);
        assertThat(testResident.getDateArrivee()).isEqualTo(UPDATED_DATE_ARRIVEE);
        assertThat(testResident.getChambre()).isEqualTo(UPDATED_CHAMBRE);
        assertThat(testResident.getTaille()).isEqualTo(UPDATED_TAILLE);
        assertThat(testResident.getDenutrition()).isEqualTo(UPDATED_DENUTRITION);
        assertThat(testResident.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void putNonExistingResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // Create the Resident
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, residentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(residentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // Create the Resident
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(residentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // Create the Resident
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(residentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateResidentWithPatch() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        int databaseSizeBeforeUpdate = residentRepository.findAll().size();

        // Update the resident using partial update
        Resident partialUpdatedResident = new Resident();
        partialUpdatedResident.setId(resident.getId());

        partialUpdatedResident
            .nom(UPDATED_NOM)
            .sexe(UPDATED_SEXE)
            .dateArrivee(UPDATED_DATE_ARRIVEE)
            .chambre(UPDATED_CHAMBRE)
            .taille(UPDATED_TAILLE)
            .denutrition(UPDATED_DENUTRITION)
            .deleted(UPDATED_DELETED);

        restResidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResident.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResident))
            )
            .andExpect(status().isOk());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
        Resident testResident = residentList.get(residentList.size() - 1);
        assertThat(testResident.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testResident.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testResident.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testResident.getDateNaissance()).isEqualTo(DEFAULT_DATE_NAISSANCE);
        assertThat(testResident.getSexe()).isEqualTo(UPDATED_SEXE);
        assertThat(testResident.getDateArrivee()).isEqualTo(UPDATED_DATE_ARRIVEE);
        assertThat(testResident.getChambre()).isEqualTo(UPDATED_CHAMBRE);
        assertThat(testResident.getTaille()).isEqualTo(UPDATED_TAILLE);
        assertThat(testResident.getDenutrition()).isEqualTo(UPDATED_DENUTRITION);
        assertThat(testResident.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void fullUpdateResidentWithPatch() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        int databaseSizeBeforeUpdate = residentRepository.findAll().size();

        // Update the resident using partial update
        Resident partialUpdatedResident = new Resident();
        partialUpdatedResident.setId(resident.getId());

        partialUpdatedResident
            .numero(UPDATED_NUMERO)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .sexe(UPDATED_SEXE)
            .dateArrivee(UPDATED_DATE_ARRIVEE)
            .chambre(UPDATED_CHAMBRE)
            .taille(UPDATED_TAILLE)
            .denutrition(UPDATED_DENUTRITION)
            .deleted(UPDATED_DELETED);

        restResidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResident.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResident))
            )
            .andExpect(status().isOk());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
        Resident testResident = residentList.get(residentList.size() - 1);
        assertThat(testResident.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testResident.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testResident.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testResident.getDateNaissance()).isEqualTo(UPDATED_DATE_NAISSANCE);
        assertThat(testResident.getSexe()).isEqualTo(UPDATED_SEXE);
        assertThat(testResident.getDateArrivee()).isEqualTo(UPDATED_DATE_ARRIVEE);
        assertThat(testResident.getChambre()).isEqualTo(UPDATED_CHAMBRE);
        assertThat(testResident.getTaille()).isEqualTo(UPDATED_TAILLE);
        assertThat(testResident.getDenutrition()).isEqualTo(UPDATED_DENUTRITION);
        assertThat(testResident.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // Create the Resident
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, residentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(residentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // Create the Resident
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(residentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResident() throws Exception {
        int databaseSizeBeforeUpdate = residentRepository.findAll().size();
        resident.setId(count.incrementAndGet());

        // Create the Resident
        ResidentDTO residentDTO = residentMapper.toDto(resident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResidentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(residentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Resident in the database
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteResident() throws Exception {
        // Initialize the database
        residentRepository.saveAndFlush(resident);

        int databaseSizeBeforeDelete = residentRepository.findAll().size();

        // Delete the resident
        restResidentMockMvc
            .perform(delete(ENTITY_API_URL_ID, resident.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Resident> residentList = residentRepository.findAll();
        assertThat(residentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
