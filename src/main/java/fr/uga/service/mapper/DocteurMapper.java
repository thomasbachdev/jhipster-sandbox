package fr.uga.service.mapper;

import fr.uga.domain.Docteur;
import fr.uga.domain.User;
import fr.uga.service.dto.DocteurDTO;
import fr.uga.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Docteur} and its DTO {@link DocteurDTO}.
 */
@Mapper(componentModel = "spring")
public interface DocteurMapper extends EntityMapper<DocteurDTO, Docteur> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    DocteurDTO toDto(Docteur s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
