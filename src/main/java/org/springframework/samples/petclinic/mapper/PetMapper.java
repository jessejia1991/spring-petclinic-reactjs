package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.rest.dto.PetDto;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {PetTypeMapper.class, VisitMapper.class})
public interface PetMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "label", expression = "java(pet.getLabel())")
    PetDto toPetDto(Pet pet);

    @Mapping(source = "ownerId", target = "owner.id")
    Pet toPet(PetDto petDto);

    List<PetDto> toPetsDto(Collection<Pet> pets);

    List<Pet> toPets(Collection<PetDto> petDtos);
}
