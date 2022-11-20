package fr.uga.service;

import fr.uga.domain.Resident;
import fr.uga.repository.ResidentRepository;
import fr.uga.service.dto.ResidentDTO;
import fr.uga.service.mapper.ResidentMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Resident}.
 */
@Service
@Transactional
public class ResidentService {

    private final Logger log = LoggerFactory.getLogger(ResidentService.class);

    private final ResidentRepository residentRepository;

    private final ResidentMapper residentMapper;

    public ResidentService(ResidentRepository residentRepository, ResidentMapper residentMapper) {
        this.residentRepository = residentRepository;
        this.residentMapper = residentMapper;
    }

    /**
     * Save a resident.
     *
     * @param residentDTO the entity to save.
     * @return the persisted entity.
     */
    public ResidentDTO save(ResidentDTO residentDTO) {
        log.debug("Request to save Resident : {}", residentDTO);
        Resident resident = residentMapper.toEntity(residentDTO);
        resident = residentRepository.save(resident);
        return residentMapper.toDto(resident);
    }

    /**
     * Update a resident.
     *
     * @param residentDTO the entity to save.
     * @return the persisted entity.
     */
    public ResidentDTO update(ResidentDTO residentDTO) {
        log.debug("Request to update Resident : {}", residentDTO);
        Resident resident = residentMapper.toEntity(residentDTO);
        resident = residentRepository.save(resident);
        return residentMapper.toDto(resident);
    }

    /**
     * Partially update a resident.
     *
     * @param residentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ResidentDTO> partialUpdate(ResidentDTO residentDTO) {
        log.debug("Request to partially update Resident : {}", residentDTO);

        return residentRepository
            .findById(residentDTO.getId())
            .map(existingResident -> {
                residentMapper.partialUpdate(existingResident, residentDTO);

                return existingResident;
            })
            .map(residentRepository::save)
            .map(residentMapper::toDto);
    }

    /**
     * Get all the residents.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ResidentDTO> findAll() {
        log.debug("Request to get all Residents");
        return residentRepository.findAll().stream().map(residentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one resident by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ResidentDTO> findOne(Long id) {
        log.debug("Request to get Resident : {}", id);
        return residentRepository.findById(id).map(residentMapper::toDto);
    }

    /**
     * Delete the resident by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Resident : {}", id);
        residentRepository.deleteById(id);
    }
}
