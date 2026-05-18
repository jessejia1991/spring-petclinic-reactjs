package org.springframework.samples.petclinic.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.PetMapper;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api")
public class PetRestController {

    private final ClinicService clinicService;
    private final PetMapper petMapper;

    @Autowired
    public PetRestController(ClinicService clinicService, PetMapper petMapper) {
        this.clinicService = clinicService;
        this.petMapper = petMapper;
    }

    @GetMapping("/pets/{petId}")
    public ResponseEntity<PetDto> getPet(@PathVariable("petId") int petId) {
        Pet pet = clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(petMapper.toDto(pet), HttpStatus.OK);
    }

    /**
     * GET /api/pets/{petId}/label
     * Returns the current label of the pet.
     */
    @GetMapping("/pets/{petId}/label")
    public ResponseEntity<Map<String, String>> getPetLabel(@PathVariable("petId") int petId) {
        Pet pet = clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String label = pet.getLabel();
        return new ResponseEntity<>(Map.of("label", label != null ? label : ""), HttpStatus.OK);
    }

    /**
     * PATCH /api/pets/{petId}/label
     * Sets the label of the pet.
     * Expects a JSON body: { "label": "some label text" }
     */
    @PatchMapping("/pets/{petId}/label")
    public ResponseEntity<PetDto> updatePetLabel(
            @PathVariable("petId") int petId,
            @Valid @RequestBody PetLabelRequest labelRequest) {

        Pet pet = clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        pet.setLabel(labelRequest.getLabel());
        clinicService.savePet(pet);

        return new ResponseEntity<>(petMapper.toDto(pet), HttpStatus.OK);
    }

    /**
     * Simple request body DTO for the label PATCH endpoint.
     */
    public static class PetLabelRequest {
        private String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
