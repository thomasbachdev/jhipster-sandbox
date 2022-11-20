package fr.uga.web.rest;

import static fr.uga.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.uga.IntegrationTest;
import fr.uga.domain.Examen;
import fr.uga.domain.Resident;
import fr.uga.domain.Soignant;
import fr.uga.repository.ExamenRepository;
import fr.uga.service.criteria.ExamenCriteria;
import fr.uga.service.dto.ExamenDTO;
import fr.uga.service.mapper.ExamenMapper;
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
 * Integration tests for the {@link ExamenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExamenResourceIT {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Float DEFAULT_POIDS = 0F;
    private static final Float UPDATED_POIDS = 1F;
    private static final Float SMALLER_POIDS = 0F - 1F;

    private static final Float DEFAULT_ALBUMINE = 0F;
    private static final Float UPDATED_ALBUMINE = 1F;
    private static final Float SMALLER_ALBUMINE = 0F - 1F;

    private static final Float DEFAULT_IMC = 1F;
    private static final Float UPDATED_IMC = 2F;
    private static final Float SMALLER_IMC = 1F - 1F;

    private static final Integer DEFAULT_EPA = 0;
    private static final Integer UPDATED_EPA = 1;
    private static final Integer SMALLER_EPA = 0 - 1;

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String ENTITY_API_URL = "/api/examen";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private ExamenMapper examenMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamenMockMvc;

    private Examen examen;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Examen createEntity(EntityManager em) {
        Examen examen = new Examen()
            .date(DEFAULT_DATE)
            .poids(DEFAULT_POIDS)
            .albumine(DEFAULT_ALBUMINE)
            .imc(DEFAULT_IMC)
            .epa(DEFAULT_EPA)
            .commentaire(DEFAULT_COMMENTAIRE)
            .deleted(DEFAULT_DELETED);
        return examen;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Examen createUpdatedEntity(EntityManager em) {
        Examen examen = new Examen()
            .date(UPDATED_DATE)
            .poids(UPDATED_POIDS)
            .albumine(UPDATED_ALBUMINE)
            .imc(UPDATED_IMC)
            .epa(UPDATED_EPA)
            .commentaire(UPDATED_COMMENTAIRE)
            .deleted(UPDATED_DELETED);
        return examen;
    }

    @BeforeEach
    public void initTest() {
        examen = createEntity(em);
    }

    @Test
    @Transactional
    void createExamen() throws Exception {
        int databaseSizeBeforeCreate = examenRepository.findAll().size();
        // Create the Examen
        ExamenDTO examenDTO = examenMapper.toDto(examen);
        restExamenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(examenDTO)))
            .andExpect(status().isCreated());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeCreate + 1);
        Examen testExamen = examenList.get(examenList.size() - 1);
        assertThat(testExamen.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testExamen.getPoids()).isEqualTo(DEFAULT_POIDS);
        assertThat(testExamen.getAlbumine()).isEqualTo(DEFAULT_ALBUMINE);
        assertThat(testExamen.getImc()).isEqualTo(DEFAULT_IMC);
        assertThat(testExamen.getEpa()).isEqualTo(DEFAULT_EPA);
        assertThat(testExamen.getCommentaire()).isEqualTo(DEFAULT_COMMENTAIRE);
        assertThat(testExamen.getDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    void createExamenWithExistingId() throws Exception {
        // Create the Examen with an existing ID
        examen.setId(1L);
        ExamenDTO examenDTO = examenMapper.toDto(examen);

        int databaseSizeBeforeCreate = examenRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(examenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = examenRepository.findAll().size();
        // set the field null
        examen.setDate(null);

        // Create the Examen, which fails.
        ExamenDTO examenDTO = examenMapper.toDto(examen);

        restExamenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(examenDTO)))
            .andExpect(status().isBadRequest());

        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExamen() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList
        restExamenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examen.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].poids").value(hasItem(DEFAULT_POIDS.doubleValue())))
            .andExpect(jsonPath("$.[*].albumine").value(hasItem(DEFAULT_ALBUMINE.doubleValue())))
            .andExpect(jsonPath("$.[*].imc").value(hasItem(DEFAULT_IMC.doubleValue())))
            .andExpect(jsonPath("$.[*].epa").value(hasItem(DEFAULT_EPA)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    void getExamen() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get the examen
        restExamenMockMvc
            .perform(get(ENTITY_API_URL_ID, examen.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(examen.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.poids").value(DEFAULT_POIDS.doubleValue()))
            .andExpect(jsonPath("$.albumine").value(DEFAULT_ALBUMINE.doubleValue()))
            .andExpect(jsonPath("$.imc").value(DEFAULT_IMC.doubleValue()))
            .andExpect(jsonPath("$.epa").value(DEFAULT_EPA))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getExamenByIdFiltering() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        Long id = examen.getId();

        defaultExamenShouldBeFound("id.equals=" + id);
        defaultExamenShouldNotBeFound("id.notEquals=" + id);

        defaultExamenShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultExamenShouldNotBeFound("id.greaterThan=" + id);

        defaultExamenShouldBeFound("id.lessThanOrEqual=" + id);
        defaultExamenShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllExamenByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where date equals to DEFAULT_DATE
        defaultExamenShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the examenList where date equals to UPDATED_DATE
        defaultExamenShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllExamenByDateIsInShouldWork() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where date in DEFAULT_DATE or UPDATED_DATE
        defaultExamenShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the examenList where date equals to UPDATED_DATE
        defaultExamenShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllExamenByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where date is not null
        defaultExamenShouldBeFound("date.specified=true");

        // Get all the examenList where date is null
        defaultExamenShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllExamenByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where date is greater than or equal to DEFAULT_DATE
        defaultExamenShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the examenList where date is greater than or equal to UPDATED_DATE
        defaultExamenShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllExamenByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where date is less than or equal to DEFAULT_DATE
        defaultExamenShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the examenList where date is less than or equal to SMALLER_DATE
        defaultExamenShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllExamenByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where date is less than DEFAULT_DATE
        defaultExamenShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the examenList where date is less than UPDATED_DATE
        defaultExamenShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllExamenByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where date is greater than DEFAULT_DATE
        defaultExamenShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the examenList where date is greater than SMALLER_DATE
        defaultExamenShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllExamenByPoidsIsEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where poids equals to DEFAULT_POIDS
        defaultExamenShouldBeFound("poids.equals=" + DEFAULT_POIDS);

        // Get all the examenList where poids equals to UPDATED_POIDS
        defaultExamenShouldNotBeFound("poids.equals=" + UPDATED_POIDS);
    }

    @Test
    @Transactional
    void getAllExamenByPoidsIsInShouldWork() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where poids in DEFAULT_POIDS or UPDATED_POIDS
        defaultExamenShouldBeFound("poids.in=" + DEFAULT_POIDS + "," + UPDATED_POIDS);

        // Get all the examenList where poids equals to UPDATED_POIDS
        defaultExamenShouldNotBeFound("poids.in=" + UPDATED_POIDS);
    }

    @Test
    @Transactional
    void getAllExamenByPoidsIsNullOrNotNull() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where poids is not null
        defaultExamenShouldBeFound("poids.specified=true");

        // Get all the examenList where poids is null
        defaultExamenShouldNotBeFound("poids.specified=false");
    }

    @Test
    @Transactional
    void getAllExamenByPoidsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where poids is greater than or equal to DEFAULT_POIDS
        defaultExamenShouldBeFound("poids.greaterThanOrEqual=" + DEFAULT_POIDS);

        // Get all the examenList where poids is greater than or equal to UPDATED_POIDS
        defaultExamenShouldNotBeFound("poids.greaterThanOrEqual=" + UPDATED_POIDS);
    }

    @Test
    @Transactional
    void getAllExamenByPoidsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where poids is less than or equal to DEFAULT_POIDS
        defaultExamenShouldBeFound("poids.lessThanOrEqual=" + DEFAULT_POIDS);

        // Get all the examenList where poids is less than or equal to SMALLER_POIDS
        defaultExamenShouldNotBeFound("poids.lessThanOrEqual=" + SMALLER_POIDS);
    }

    @Test
    @Transactional
    void getAllExamenByPoidsIsLessThanSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where poids is less than DEFAULT_POIDS
        defaultExamenShouldNotBeFound("poids.lessThan=" + DEFAULT_POIDS);

        // Get all the examenList where poids is less than UPDATED_POIDS
        defaultExamenShouldBeFound("poids.lessThan=" + UPDATED_POIDS);
    }

    @Test
    @Transactional
    void getAllExamenByPoidsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where poids is greater than DEFAULT_POIDS
        defaultExamenShouldNotBeFound("poids.greaterThan=" + DEFAULT_POIDS);

        // Get all the examenList where poids is greater than SMALLER_POIDS
        defaultExamenShouldBeFound("poids.greaterThan=" + SMALLER_POIDS);
    }

    @Test
    @Transactional
    void getAllExamenByAlbumineIsEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where albumine equals to DEFAULT_ALBUMINE
        defaultExamenShouldBeFound("albumine.equals=" + DEFAULT_ALBUMINE);

        // Get all the examenList where albumine equals to UPDATED_ALBUMINE
        defaultExamenShouldNotBeFound("albumine.equals=" + UPDATED_ALBUMINE);
    }

    @Test
    @Transactional
    void getAllExamenByAlbumineIsInShouldWork() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where albumine in DEFAULT_ALBUMINE or UPDATED_ALBUMINE
        defaultExamenShouldBeFound("albumine.in=" + DEFAULT_ALBUMINE + "," + UPDATED_ALBUMINE);

        // Get all the examenList where albumine equals to UPDATED_ALBUMINE
        defaultExamenShouldNotBeFound("albumine.in=" + UPDATED_ALBUMINE);
    }

    @Test
    @Transactional
    void getAllExamenByAlbumineIsNullOrNotNull() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where albumine is not null
        defaultExamenShouldBeFound("albumine.specified=true");

        // Get all the examenList where albumine is null
        defaultExamenShouldNotBeFound("albumine.specified=false");
    }

    @Test
    @Transactional
    void getAllExamenByAlbumineIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where albumine is greater than or equal to DEFAULT_ALBUMINE
        defaultExamenShouldBeFound("albumine.greaterThanOrEqual=" + DEFAULT_ALBUMINE);

        // Get all the examenList where albumine is greater than or equal to UPDATED_ALBUMINE
        defaultExamenShouldNotBeFound("albumine.greaterThanOrEqual=" + UPDATED_ALBUMINE);
    }

    @Test
    @Transactional
    void getAllExamenByAlbumineIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where albumine is less than or equal to DEFAULT_ALBUMINE
        defaultExamenShouldBeFound("albumine.lessThanOrEqual=" + DEFAULT_ALBUMINE);

        // Get all the examenList where albumine is less than or equal to SMALLER_ALBUMINE
        defaultExamenShouldNotBeFound("albumine.lessThanOrEqual=" + SMALLER_ALBUMINE);
    }

    @Test
    @Transactional
    void getAllExamenByAlbumineIsLessThanSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where albumine is less than DEFAULT_ALBUMINE
        defaultExamenShouldNotBeFound("albumine.lessThan=" + DEFAULT_ALBUMINE);

        // Get all the examenList where albumine is less than UPDATED_ALBUMINE
        defaultExamenShouldBeFound("albumine.lessThan=" + UPDATED_ALBUMINE);
    }

    @Test
    @Transactional
    void getAllExamenByAlbumineIsGreaterThanSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where albumine is greater than DEFAULT_ALBUMINE
        defaultExamenShouldNotBeFound("albumine.greaterThan=" + DEFAULT_ALBUMINE);

        // Get all the examenList where albumine is greater than SMALLER_ALBUMINE
        defaultExamenShouldBeFound("albumine.greaterThan=" + SMALLER_ALBUMINE);
    }

    @Test
    @Transactional
    void getAllExamenByImcIsEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where imc equals to DEFAULT_IMC
        defaultExamenShouldBeFound("imc.equals=" + DEFAULT_IMC);

        // Get all the examenList where imc equals to UPDATED_IMC
        defaultExamenShouldNotBeFound("imc.equals=" + UPDATED_IMC);
    }

    @Test
    @Transactional
    void getAllExamenByImcIsInShouldWork() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where imc in DEFAULT_IMC or UPDATED_IMC
        defaultExamenShouldBeFound("imc.in=" + DEFAULT_IMC + "," + UPDATED_IMC);

        // Get all the examenList where imc equals to UPDATED_IMC
        defaultExamenShouldNotBeFound("imc.in=" + UPDATED_IMC);
    }

    @Test
    @Transactional
    void getAllExamenByImcIsNullOrNotNull() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where imc is not null
        defaultExamenShouldBeFound("imc.specified=true");

        // Get all the examenList where imc is null
        defaultExamenShouldNotBeFound("imc.specified=false");
    }

    @Test
    @Transactional
    void getAllExamenByImcIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where imc is greater than or equal to DEFAULT_IMC
        defaultExamenShouldBeFound("imc.greaterThanOrEqual=" + DEFAULT_IMC);

        // Get all the examenList where imc is greater than or equal to UPDATED_IMC
        defaultExamenShouldNotBeFound("imc.greaterThanOrEqual=" + UPDATED_IMC);
    }

    @Test
    @Transactional
    void getAllExamenByImcIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where imc is less than or equal to DEFAULT_IMC
        defaultExamenShouldBeFound("imc.lessThanOrEqual=" + DEFAULT_IMC);

        // Get all the examenList where imc is less than or equal to SMALLER_IMC
        defaultExamenShouldNotBeFound("imc.lessThanOrEqual=" + SMALLER_IMC);
    }

    @Test
    @Transactional
    void getAllExamenByImcIsLessThanSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where imc is less than DEFAULT_IMC
        defaultExamenShouldNotBeFound("imc.lessThan=" + DEFAULT_IMC);

        // Get all the examenList where imc is less than UPDATED_IMC
        defaultExamenShouldBeFound("imc.lessThan=" + UPDATED_IMC);
    }

    @Test
    @Transactional
    void getAllExamenByImcIsGreaterThanSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where imc is greater than DEFAULT_IMC
        defaultExamenShouldNotBeFound("imc.greaterThan=" + DEFAULT_IMC);

        // Get all the examenList where imc is greater than SMALLER_IMC
        defaultExamenShouldBeFound("imc.greaterThan=" + SMALLER_IMC);
    }

    @Test
    @Transactional
    void getAllExamenByEpaIsEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where epa equals to DEFAULT_EPA
        defaultExamenShouldBeFound("epa.equals=" + DEFAULT_EPA);

        // Get all the examenList where epa equals to UPDATED_EPA
        defaultExamenShouldNotBeFound("epa.equals=" + UPDATED_EPA);
    }

    @Test
    @Transactional
    void getAllExamenByEpaIsInShouldWork() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where epa in DEFAULT_EPA or UPDATED_EPA
        defaultExamenShouldBeFound("epa.in=" + DEFAULT_EPA + "," + UPDATED_EPA);

        // Get all the examenList where epa equals to UPDATED_EPA
        defaultExamenShouldNotBeFound("epa.in=" + UPDATED_EPA);
    }

    @Test
    @Transactional
    void getAllExamenByEpaIsNullOrNotNull() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where epa is not null
        defaultExamenShouldBeFound("epa.specified=true");

        // Get all the examenList where epa is null
        defaultExamenShouldNotBeFound("epa.specified=false");
    }

    @Test
    @Transactional
    void getAllExamenByEpaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where epa is greater than or equal to DEFAULT_EPA
        defaultExamenShouldBeFound("epa.greaterThanOrEqual=" + DEFAULT_EPA);

        // Get all the examenList where epa is greater than or equal to (DEFAULT_EPA + 1)
        defaultExamenShouldNotBeFound("epa.greaterThanOrEqual=" + (DEFAULT_EPA + 1));
    }

    @Test
    @Transactional
    void getAllExamenByEpaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where epa is less than or equal to DEFAULT_EPA
        defaultExamenShouldBeFound("epa.lessThanOrEqual=" + DEFAULT_EPA);

        // Get all the examenList where epa is less than or equal to SMALLER_EPA
        defaultExamenShouldNotBeFound("epa.lessThanOrEqual=" + SMALLER_EPA);
    }

    @Test
    @Transactional
    void getAllExamenByEpaIsLessThanSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where epa is less than DEFAULT_EPA
        defaultExamenShouldNotBeFound("epa.lessThan=" + DEFAULT_EPA);

        // Get all the examenList where epa is less than (DEFAULT_EPA + 1)
        defaultExamenShouldBeFound("epa.lessThan=" + (DEFAULT_EPA + 1));
    }

    @Test
    @Transactional
    void getAllExamenByEpaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where epa is greater than DEFAULT_EPA
        defaultExamenShouldNotBeFound("epa.greaterThan=" + DEFAULT_EPA);

        // Get all the examenList where epa is greater than SMALLER_EPA
        defaultExamenShouldBeFound("epa.greaterThan=" + SMALLER_EPA);
    }

    @Test
    @Transactional
    void getAllExamenByCommentaireIsEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where commentaire equals to DEFAULT_COMMENTAIRE
        defaultExamenShouldBeFound("commentaire.equals=" + DEFAULT_COMMENTAIRE);

        // Get all the examenList where commentaire equals to UPDATED_COMMENTAIRE
        defaultExamenShouldNotBeFound("commentaire.equals=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllExamenByCommentaireIsInShouldWork() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where commentaire in DEFAULT_COMMENTAIRE or UPDATED_COMMENTAIRE
        defaultExamenShouldBeFound("commentaire.in=" + DEFAULT_COMMENTAIRE + "," + UPDATED_COMMENTAIRE);

        // Get all the examenList where commentaire equals to UPDATED_COMMENTAIRE
        defaultExamenShouldNotBeFound("commentaire.in=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllExamenByCommentaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where commentaire is not null
        defaultExamenShouldBeFound("commentaire.specified=true");

        // Get all the examenList where commentaire is null
        defaultExamenShouldNotBeFound("commentaire.specified=false");
    }

    @Test
    @Transactional
    void getAllExamenByCommentaireContainsSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where commentaire contains DEFAULT_COMMENTAIRE
        defaultExamenShouldBeFound("commentaire.contains=" + DEFAULT_COMMENTAIRE);

        // Get all the examenList where commentaire contains UPDATED_COMMENTAIRE
        defaultExamenShouldNotBeFound("commentaire.contains=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllExamenByCommentaireNotContainsSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where commentaire does not contain DEFAULT_COMMENTAIRE
        defaultExamenShouldNotBeFound("commentaire.doesNotContain=" + DEFAULT_COMMENTAIRE);

        // Get all the examenList where commentaire does not contain UPDATED_COMMENTAIRE
        defaultExamenShouldBeFound("commentaire.doesNotContain=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllExamenByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where deleted equals to DEFAULT_DELETED
        defaultExamenShouldBeFound("deleted.equals=" + DEFAULT_DELETED);

        // Get all the examenList where deleted equals to UPDATED_DELETED
        defaultExamenShouldNotBeFound("deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllExamenByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where deleted in DEFAULT_DELETED or UPDATED_DELETED
        defaultExamenShouldBeFound("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED);

        // Get all the examenList where deleted equals to UPDATED_DELETED
        defaultExamenShouldNotBeFound("deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllExamenByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        // Get all the examenList where deleted is not null
        defaultExamenShouldBeFound("deleted.specified=true");

        // Get all the examenList where deleted is null
        defaultExamenShouldNotBeFound("deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllExamenByResidentIsEqualToSomething() throws Exception {
        Resident resident;
        if (TestUtil.findAll(em, Resident.class).isEmpty()) {
            examenRepository.saveAndFlush(examen);
            resident = ResidentResourceIT.createEntity(em);
        } else {
            resident = TestUtil.findAll(em, Resident.class).get(0);
        }
        em.persist(resident);
        em.flush();
        examen.setResident(resident);
        examenRepository.saveAndFlush(examen);
        Long residentId = resident.getId();

        // Get all the examenList where resident equals to residentId
        defaultExamenShouldBeFound("residentId.equals=" + residentId);

        // Get all the examenList where resident equals to (residentId + 1)
        defaultExamenShouldNotBeFound("residentId.equals=" + (residentId + 1));
    }

    @Test
    @Transactional
    void getAllExamenBySoignantIsEqualToSomething() throws Exception {
        Soignant soignant;
        if (TestUtil.findAll(em, Soignant.class).isEmpty()) {
            examenRepository.saveAndFlush(examen);
            soignant = SoignantResourceIT.createEntity(em);
        } else {
            soignant = TestUtil.findAll(em, Soignant.class).get(0);
        }
        em.persist(soignant);
        em.flush();
        examen.setSoignant(soignant);
        examenRepository.saveAndFlush(examen);
        Long soignantId = soignant.getId();

        // Get all the examenList where soignant equals to soignantId
        defaultExamenShouldBeFound("soignantId.equals=" + soignantId);

        // Get all the examenList where soignant equals to (soignantId + 1)
        defaultExamenShouldNotBeFound("soignantId.equals=" + (soignantId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultExamenShouldBeFound(String filter) throws Exception {
        restExamenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examen.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].poids").value(hasItem(DEFAULT_POIDS.doubleValue())))
            .andExpect(jsonPath("$.[*].albumine").value(hasItem(DEFAULT_ALBUMINE.doubleValue())))
            .andExpect(jsonPath("$.[*].imc").value(hasItem(DEFAULT_IMC.doubleValue())))
            .andExpect(jsonPath("$.[*].epa").value(hasItem(DEFAULT_EPA)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));

        // Check, that the count call also returns 1
        restExamenMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultExamenShouldNotBeFound(String filter) throws Exception {
        restExamenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restExamenMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingExamen() throws Exception {
        // Get the examen
        restExamenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExamen() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        int databaseSizeBeforeUpdate = examenRepository.findAll().size();

        // Update the examen
        Examen updatedExamen = examenRepository.findById(examen.getId()).get();
        // Disconnect from session so that the updates on updatedExamen are not directly saved in db
        em.detach(updatedExamen);
        updatedExamen
            .date(UPDATED_DATE)
            .poids(UPDATED_POIDS)
            .albumine(UPDATED_ALBUMINE)
            .imc(UPDATED_IMC)
            .epa(UPDATED_EPA)
            .commentaire(UPDATED_COMMENTAIRE)
            .deleted(UPDATED_DELETED);
        ExamenDTO examenDTO = examenMapper.toDto(updatedExamen);

        restExamenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(examenDTO))
            )
            .andExpect(status().isOk());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeUpdate);
        Examen testExamen = examenList.get(examenList.size() - 1);
        assertThat(testExamen.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testExamen.getPoids()).isEqualTo(UPDATED_POIDS);
        assertThat(testExamen.getAlbumine()).isEqualTo(UPDATED_ALBUMINE);
        assertThat(testExamen.getImc()).isEqualTo(UPDATED_IMC);
        assertThat(testExamen.getEpa()).isEqualTo(UPDATED_EPA);
        assertThat(testExamen.getCommentaire()).isEqualTo(UPDATED_COMMENTAIRE);
        assertThat(testExamen.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void putNonExistingExamen() throws Exception {
        int databaseSizeBeforeUpdate = examenRepository.findAll().size();
        examen.setId(count.incrementAndGet());

        // Create the Examen
        ExamenDTO examenDTO = examenMapper.toDto(examen);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(examenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExamen() throws Exception {
        int databaseSizeBeforeUpdate = examenRepository.findAll().size();
        examen.setId(count.incrementAndGet());

        // Create the Examen
        ExamenDTO examenDTO = examenMapper.toDto(examen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(examenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExamen() throws Exception {
        int databaseSizeBeforeUpdate = examenRepository.findAll().size();
        examen.setId(count.incrementAndGet());

        // Create the Examen
        ExamenDTO examenDTO = examenMapper.toDto(examen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(examenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamenWithPatch() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        int databaseSizeBeforeUpdate = examenRepository.findAll().size();

        // Update the examen using partial update
        Examen partialUpdatedExamen = new Examen();
        partialUpdatedExamen.setId(examen.getId());

        partialUpdatedExamen.poids(UPDATED_POIDS).albumine(UPDATED_ALBUMINE).epa(UPDATED_EPA).deleted(UPDATED_DELETED);

        restExamenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamen.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExamen))
            )
            .andExpect(status().isOk());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeUpdate);
        Examen testExamen = examenList.get(examenList.size() - 1);
        assertThat(testExamen.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testExamen.getPoids()).isEqualTo(UPDATED_POIDS);
        assertThat(testExamen.getAlbumine()).isEqualTo(UPDATED_ALBUMINE);
        assertThat(testExamen.getImc()).isEqualTo(DEFAULT_IMC);
        assertThat(testExamen.getEpa()).isEqualTo(UPDATED_EPA);
        assertThat(testExamen.getCommentaire()).isEqualTo(DEFAULT_COMMENTAIRE);
        assertThat(testExamen.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void fullUpdateExamenWithPatch() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        int databaseSizeBeforeUpdate = examenRepository.findAll().size();

        // Update the examen using partial update
        Examen partialUpdatedExamen = new Examen();
        partialUpdatedExamen.setId(examen.getId());

        partialUpdatedExamen
            .date(UPDATED_DATE)
            .poids(UPDATED_POIDS)
            .albumine(UPDATED_ALBUMINE)
            .imc(UPDATED_IMC)
            .epa(UPDATED_EPA)
            .commentaire(UPDATED_COMMENTAIRE)
            .deleted(UPDATED_DELETED);

        restExamenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamen.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExamen))
            )
            .andExpect(status().isOk());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeUpdate);
        Examen testExamen = examenList.get(examenList.size() - 1);
        assertThat(testExamen.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testExamen.getPoids()).isEqualTo(UPDATED_POIDS);
        assertThat(testExamen.getAlbumine()).isEqualTo(UPDATED_ALBUMINE);
        assertThat(testExamen.getImc()).isEqualTo(UPDATED_IMC);
        assertThat(testExamen.getEpa()).isEqualTo(UPDATED_EPA);
        assertThat(testExamen.getCommentaire()).isEqualTo(UPDATED_COMMENTAIRE);
        assertThat(testExamen.getDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingExamen() throws Exception {
        int databaseSizeBeforeUpdate = examenRepository.findAll().size();
        examen.setId(count.incrementAndGet());

        // Create the Examen
        ExamenDTO examenDTO = examenMapper.toDto(examen);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, examenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(examenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExamen() throws Exception {
        int databaseSizeBeforeUpdate = examenRepository.findAll().size();
        examen.setId(count.incrementAndGet());

        // Create the Examen
        ExamenDTO examenDTO = examenMapper.toDto(examen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(examenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExamen() throws Exception {
        int databaseSizeBeforeUpdate = examenRepository.findAll().size();
        examen.setId(count.incrementAndGet());

        // Create the Examen
        ExamenDTO examenDTO = examenMapper.toDto(examen);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamenMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(examenDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Examen in the database
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExamen() throws Exception {
        // Initialize the database
        examenRepository.saveAndFlush(examen);

        int databaseSizeBeforeDelete = examenRepository.findAll().size();

        // Delete the examen
        restExamenMockMvc
            .perform(delete(ENTITY_API_URL_ID, examen.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Examen> examenList = examenRepository.findAll();
        assertThat(examenList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
