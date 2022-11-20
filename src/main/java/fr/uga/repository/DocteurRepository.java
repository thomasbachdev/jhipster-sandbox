package fr.uga.repository;

import fr.uga.domain.Docteur;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Docteur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocteurRepository extends JpaRepository<Docteur, Long>, JpaSpecificationExecutor<Docteur> {}
