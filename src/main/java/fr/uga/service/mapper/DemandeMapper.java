package fr.uga.service.mapper;

import fr.uga.domain.Demande;
import fr.uga.domain.Docteur;
import fr.uga.domain.Resident;
import fr.uga.service.dto.DemandeDTO;
import fr.uga.service.dto.DocteurDTO;
import fr.uga.service.dto.ResidentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Demande} and its DTO {@link DemandeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DemandeMapper extends EntityMapper<DemandeDTO, Demande> {
    @Mapping(target = "resident", source = "resident", qualifiedByName = "residentId")
    @Mapping(target = "docteur", source = "docteur", qualifiedByName = "docteurId")
    DemandeDTO toDto(Demande s);

    @Named("residentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ResidentDTO toDtoResidentId(Resident resident);

    @Named("docteurId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DocteurDTO toDtoDocteurId(Docteur docteur);
}
