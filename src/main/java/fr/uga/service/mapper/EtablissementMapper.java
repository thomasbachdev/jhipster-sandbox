package fr.uga.service.mapper;

import fr.uga.domain.Etablissement;
import fr.uga.service.dto.EtablissementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Etablissement} and its DTO {@link EtablissementDTO}.
 */
@Mapper(componentModel = "spring")
public interface EtablissementMapper extends EntityMapper<EtablissementDTO, Etablissement> {}
