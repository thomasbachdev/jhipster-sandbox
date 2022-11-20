package fr.uga.service;

import fr.uga.domain.Soignant;
import fr.uga.repository.SoignantRepository;
import fr.uga.service.dto.SoignantDTO;
import fr.uga.service.mapper.SoignantMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Soignant}.
 */
@Service
@Transactional
public class SoignantService {

    private final Logger log = LoggerFactory.getLogger(SoignantService.class);

    private final SoignantRepository soignantRepository;

    private final SoignantMapper soignantMapper;

    public SoignantService(SoignantRepository soignantRepository, SoignantMapper soignantMapper) {
        this.soignantRepository = soignantRepository;
        this.soignantMapper = soignantMapper;
    }

    /**
     * Save a soignant.
     *
     * @param soignantDTO the entity to save.
     * @return the persisted entity.
     */
    public SoignantDTO save(SoignantDTO soignantDTO) {
        log.debug("Request to save Soignant : {}", soignantDTO);
        Soignant soignant = soignantMapper.toEntity(soignantDTO);
        soignant = soignantRepository.save(soignant);
        return soignantMapper.toDto(soignant);
    }

    /**
     * Update a soignant.
     *
     * @param soignantDTO the entity to save.
     * @return the persisted entity.
     */
    public SoignantDTO update(SoignantDTO soignantDTO) {
        log.debug("Request to update Soignant : {}", soignantDTO);
        Soignant soignant = soignantMapper.toEntity(soignantDTO);
        soignant = soignantRepository.save(soignant);
        return soignantMapper.toDto(soignant);
    }

    /**
     * Partially update a soignant.
     *
     * @param soignantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SoignantDTO> partialUpdate(SoignantDTO soignantDTO) {
        log.debug("Request to partially update Soignant : {}", soignantDTO);

        return soignantRepository
            .findById(soignantDTO.getId())
            .map(existingSoignant -> {
                soignantMapper.partialUpdate(existingSoignant, soignantDTO);

                return existingSoignant;
            })
            .map(soignantRepository::save)
            .map(soignantMapper::toDto);
    }

    /**
     * Get all the soignants.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SoignantDTO> findAll() {
        log.debug("Request to get all Soignants");
        return soignantRepository.findAll().stream().map(soignantMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one soignant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SoignantDTO> findOne(Long id) {
        log.debug("Request to get Soignant : {}", id);
        return soignantRepository.findById(id).map(soignantMapper::toDto);
    }

    /**
     * Delete the soignant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Soignant : {}", id);
        soignantRepository.deleteById(id);
    }
}
