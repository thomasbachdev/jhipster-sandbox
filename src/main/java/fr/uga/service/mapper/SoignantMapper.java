package fr.uga.service.mapper;

import fr.uga.domain.Etablissement;
import fr.uga.domain.Soignant;
import fr.uga.domain.User;
import fr.uga.service.dto.EtablissementDTO;
import fr.uga.service.dto.SoignantDTO;
import fr.uga.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Soignant} and its DTO {@link SoignantDTO}.
 */
@Mapper(componentModel = "spring")
public interface SoignantMapper extends EntityMapper<SoignantDTO, Soignant> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "etablissement", source = "etablissement", qualifiedByName = "etablissementId")
    SoignantDTO toDto(Soignant s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("etablissementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EtablissementDTO toDtoEtablissementId(Etablissement etablissement);
}
