package fr.uga.service.mapper;

import fr.uga.domain.Etablissement;
import fr.uga.domain.Resident;
import fr.uga.service.dto.EtablissementDTO;
import fr.uga.service.dto.ResidentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Resident} and its DTO {@link ResidentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ResidentMapper extends EntityMapper<ResidentDTO, Resident> {
    @Mapping(target = "etablissement", source = "etablissement", qualifiedByName = "etablissementId")
    ResidentDTO toDto(Resident s);

    @Named("etablissementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EtablissementDTO toDtoEtablissementId(Etablissement etablissement);
}
