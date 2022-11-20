package fr.uga.service;

import fr.uga.domain.Docteur;
import fr.uga.repository.DocteurRepository;
import fr.uga.service.dto.DocteurDTO;
import fr.uga.service.mapper.DocteurMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Docteur}.
 */
@Service
@Transactional
public class DocteurService {

    private final Logger log = LoggerFactory.getLogger(DocteurService.class);

    private final DocteurRepository docteurRepository;

    private final DocteurMapper docteurMapper;

    public DocteurService(DocteurRepository docteurRepository, DocteurMapper docteurMapper) {
        this.docteurRepository = docteurRepository;
        this.docteurMapper = docteurMapper;
    }

    /**
     * Save a docteur.
     *
     * @param docteurDTO the entity to save.
     * @return the persisted entity.
     */
    public DocteurDTO save(DocteurDTO docteurDTO) {
        log.debug("Request to save Docteur : {}", docteurDTO);
        Docteur docteur = docteurMapper.toEntity(docteurDTO);
        docteur = docteurRepository.save(docteur);
        return docteurMapper.toDto(docteur);
    }

    /**
     * Update a docteur.
     *
     * @param docteurDTO the entity to save.
     * @return the persisted entity.
     */
    public DocteurDTO update(DocteurDTO docteurDTO) {
        log.debug("Request to update Docteur : {}", docteurDTO);
        Docteur docteur = docteurMapper.toEntity(docteurDTO);
        docteur = docteurRepository.save(docteur);
        return docteurMapper.toDto(docteur);
    }

    /**
     * Partially update a docteur.
     *
     * @param docteurDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DocteurDTO> partialUpdate(DocteurDTO docteurDTO) {
        log.debug("Request to partially update Docteur : {}", docteurDTO);

        return docteurRepository
            .findById(docteurDTO.getId())
            .map(existingDocteur -> {
                docteurMapper.partialUpdate(existingDocteur, docteurDTO);

                return existingDocteur;
            })
            .map(docteurRepository::save)
            .map(docteurMapper::toDto);
    }

    /**
     * Get all the docteurs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<DocteurDTO> findAll() {
        log.debug("Request to get all Docteurs");
        return docteurRepository.findAll().stream().map(docteurMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one docteur by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DocteurDTO> findOne(Long id) {
        log.debug("Request to get Docteur : {}", id);
        return docteurRepository.findById(id).map(docteurMapper::toDto);
    }

    /**
     * Delete the docteur by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Docteur : {}", id);
        docteurRepository.deleteById(id);
    }
}
