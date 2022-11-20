package fr.uga.service.mapper;

import fr.uga.domain.Docteur;
import fr.uga.domain.Rappel;
import fr.uga.service.dto.DocteurDTO;
import fr.uga.service.dto.RappelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Rappel} and its DTO {@link RappelDTO}.
 */
@Mapper(componentModel = "spring")
public interface RappelMapper extends EntityMapper<RappelDTO, Rappel> {
    @Mapping(target = "docteur", source = "docteur", qualifiedByName = "docteurId")
    RappelDTO toDto(Rappel s);

    @Named("docteurId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DocteurDTO toDtoDocteurId(Docteur docteur);
}
