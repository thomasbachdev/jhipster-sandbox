package fr.uga.service;

import fr.uga.domain.Rappel;
import fr.uga.repository.RappelRepository;
import fr.uga.service.dto.RappelDTO;
import fr.uga.service.mapper.RappelMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Rappel}.
 */
@Service
@Transactional
public class RappelService {

    private final Logger log = LoggerFactory.getLogger(RappelService.class);

    private final RappelRepository rappelRepository;

    private final RappelMapper rappelMapper;

    public RappelService(RappelRepository rappelRepository, RappelMapper rappelMapper) {
        this.rappelRepository = rappelRepository;
        this.rappelMapper = rappelMapper;
    }

    /**
     * Save a rappel.
     *
     * @param rappelDTO the entity to save.
     * @return the persisted entity.
     */
    public RappelDTO save(RappelDTO rappelDTO) {
        log.debug("Request to save Rappel : {}", rappelDTO);
        Rappel rappel = rappelMapper.toEntity(rappelDTO);
        rappel = rappelRepository.save(rappel);
        return rappelMapper.toDto(rappel);
    }

    /**
     * Update a rappel.
     *
     * @param rappelDTO the entity to save.
     * @return the persisted entity.
     */
    public RappelDTO update(RappelDTO rappelDTO) {
        log.debug("Request to update Rappel : {}", rappelDTO);
        Rappel rappel = rappelMapper.toEntity(rappelDTO);
        rappel = rappelRepository.save(rappel);
        return rappelMapper.toDto(rappel);
    }

    /**
     * Partially update a rappel.
     *
     * @param rappelDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RappelDTO> partialUpdate(RappelDTO rappelDTO) {
        log.debug("Request to partially update Rappel : {}", rappelDTO);

        return rappelRepository
            .findById(rappelDTO.getId())
            .map(existingRappel -> {
                rappelMapper.partialUpdate(existingRappel, rappelDTO);

                return existingRappel;
            })
            .map(rappelRepository::save)
            .map(rappelMapper::toDto);
    }

    /**
     * Get all the rappels.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RappelDTO> findAll() {
        log.debug("Request to get all Rappels");
        return rappelRepository.findAll().stream().map(rappelMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one rappel by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RappelDTO> findOne(Long id) {
        log.debug("Request to get Rappel : {}", id);
        return rappelRepository.findById(id).map(rappelMapper::toDto);
    }

    /**
     * Delete the rappel by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Rappel : {}", id);
        rappelRepository.deleteById(id);
    }
}
