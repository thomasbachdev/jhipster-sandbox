package fr.uga.repository;

import fr.uga.domain.Soignant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Soignant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SoignantRepository extends JpaRepository<Soignant, Long>, JpaSpecificationExecutor<Soignant> {}
