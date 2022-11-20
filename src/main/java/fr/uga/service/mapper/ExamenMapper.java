package fr.uga.service.mapper;

import fr.uga.domain.Examen;
import fr.uga.domain.Resident;
import fr.uga.domain.Soignant;
import fr.uga.service.dto.ExamenDTO;
import fr.uga.service.dto.ResidentDTO;
import fr.uga.service.dto.SoignantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Examen} and its DTO {@link ExamenDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExamenMapper extends EntityMapper<ExamenDTO, Examen> {
    @Mapping(target = "resident", source = "resident", qualifiedByName = "residentId")
    @Mapping(target = "soignant", source = "soignant", qualifiedByName = "soignantId")
    ExamenDTO toDto(Examen s);

    @Named("residentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ResidentDTO toDtoResidentId(Resident resident);

    @Named("soignantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SoignantDTO toDtoSoignantId(Soignant soignant);
}
