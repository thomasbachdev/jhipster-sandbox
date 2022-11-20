package fr.uga.repository;

import fr.uga.domain.Resident;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Resident entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long>, JpaSpecificationExecutor<Resident> {}
