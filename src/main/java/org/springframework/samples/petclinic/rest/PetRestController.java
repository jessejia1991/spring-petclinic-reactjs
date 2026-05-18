package org.springframework.samples.petclinic.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.PetMapper;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api")
public class PetRestController {

    private final ClinicService clinicService;
    private final PetMapper petMapper;

    public PetRestController(ClinicService clinicService, PetMapper petMapper) {
        this.clinicService = clinicService;
        this.petMapper = petMapper;
    }

    @GetMapping("/pets")
    public ResponseEntity<List<PetDto>> getAllPets() {
        List<Pet> pets = new ArrayList<>(this.clinicService.findAllPets());
        if (pets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(petMapper.toPetDtoCollection(pets), HttpStatus.OK);
    }

    @GetMapping("/pets/{petId}")
    public ResponseEntity<PetDto> getPet(@PathVariable("petId") int petId) {
        Pet pet = this.clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(petMapper.toPetDto(pet), HttpStatus.OK);
    }

    @PostMapping("/pets")
    public ResponseEntity<PetDto> addPet(@Valid @RequestBody PetDto petDto) {
        Pet pet = petMapper.toPet(petDto);
        this.clinicService.savePet(pet);
        return new ResponseEntity<>(petMapper.toPetDto(pet), HttpStatus.CREATED);
    }

    @PutMapping("/pets/{petId}")
    public ResponseEntity<PetDto> updatePet(@PathVariable("petId") int petId,
                                             @Valid @RequestBody PetDto petDto) {
        Pet currentPet = this.clinicService.findPetById(petId);
        if (currentPet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentPet.setBirthDate(petDto.getBirthDate());
        currentPet.setName(petDto.getName());
        if (petDto.getLabel() != null) {
            currentPet.setLabel(petDto.getLabel());
        }
        this.clinicService.savePet(currentPet);
        return new ResponseEntity<>(petMapper.toPetDto(currentPet), HttpStatus.OK);
    }

    @DeleteMapping("/pets/{petId}")
    @Transactional
    public ResponseEntity<Void> deletePet(@PathVariable("petId") int petId) {
        Pet pet = this.clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deletePet(pet);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * GET /api/pets/{petId}/label
     * Returns the current label for the given pet.
     */
    @GetMapping("/pets/{petId}/label")
    public ResponseEntity<LabelResponse> getPetLabel(@PathVariable("petId") int petId) {
        Pet pet = this.clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new LabelResponse(pet.getLabel()), HttpStatus.OK);
    }

    /**
     * PATCH /api/pets/{petId}/label
     * Sets (or clears) the label for the given pet.
     */
    @PatchMapping("/pets/{petId}/label")
    public ResponseEntity<PetDto> updatePetLabel(@PathVariable("petId") int petId,
                                                  @RequestBody LabelRequest labelRequest) {
        Pet pet = this.clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        pet.setLabel(labelRequest.getLabel());
        this.clinicService.savePet(pet);
        return new ResponseEntity<>(petMapper.toPetDto(pet), HttpStatus.OK);
    }

    // --------------- Inner DTO classes for label endpoint ---------------

    public static class LabelRequest {
        private String label;

        public LabelRequest() {}

        public LabelRequest(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public static class LabelResponse {
        private String label;

        public LabelResponse() {}

        public LabelResponse(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
