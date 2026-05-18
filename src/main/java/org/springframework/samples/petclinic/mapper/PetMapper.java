package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.rest.dto.PetDto;

import java.util.Collection;
import java.util.List;

/**
 * Map Pet & PetDto using MapStruct
 */
@Mapper
public interface PetMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "label", target = "label")
    PetDto petToPetDto(Pet pet);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "label", target = "label")
    Pet petDtoToPet(PetDto petDto);

    List<PetDto> petsToPetDtos(Collection<Pet> pets);

    List<Pet> petDtosToPets(Collection<PetDto> petDtos);
}
