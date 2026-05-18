package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the pet label endpoint in {@link PetRestController}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(PetRestController.class)
public class PetRestControllerLabelTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClinicService clinicService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pet testPet;

    @Before
    public void setup() {
        testPet = new Pet();
        testPet.setId(1);
        testPet.setName("Buddy");
        // label field assumed to be added to Pet model
        testPet.setLabel(null);
    }

    /**
     * Test that GET /api/pets/{petId}/label returns the label for an existing pet.
     */
    @Test
    public void testGetPetLabel_returnsLabel() throws Exception {
        testPet.setLabel("Friendly");
        when(clinicService.findPetById(1)).thenReturn(testPet);

        mockMvc.perform(get("/api/pets/1/label")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("Friendly")));

        verify(clinicService, times(1)).findPetById(1);
    }

    /**
     * Test that GET /api/pets/{petId}/label returns 404 when pet does not exist.
     */
    @Test
    public void testGetPetLabel_petNotFound_returns404() throws Exception {
        when(clinicService.findPetById(99)).thenReturn(null);

        mockMvc.perform(get("/api/pets/99/label")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test that PUT /api/pets/{petId}/label persists and returns the updated label.
     */
    @Test
    public void testUpdatePetLabel_persistsLabel() throws Exception {
        when(clinicService.findPetById(1)).thenReturn(testPet);
        doNothing().when(clinicService).savePet(any(Pet.class));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("label", "Vaccinated");

        mockMvc.perform(put("/api/pets/1/label")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("Vaccinated")));

        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
        verify(clinicService, times(1)).savePet(petCaptor.capture());
        assertEquals("Vaccinated", petCaptor.getValue().getLabel());
    }

    /**
     * Test that PUT /api/pets/{petId}/label with an empty label clears the label.
     */
    @Test
    public void testUpdatePetLabel_clearLabel() throws Exception {
        testPet.setLabel("OldLabel");
        when(clinicService.findPetById(1)).thenReturn(testPet);
        doNothing().when(clinicService).savePet(any(Pet.class));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("label", "");

        mockMvc.perform(put("/api/pets/1/label")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("")));

        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
        verify(clinicService, times(1)).savePet(petCaptor.capture());
        assertEquals("", petCaptor.getValue().getLabel());
    }

    /**
     * Test that PUT /api/pets/{petId}/label returns 404 when pet does not exist.
     */
    @Test
    public void testUpdatePetLabel_petNotFound_returns404() throws Exception {
        when(clinicService.findPetById(99)).thenReturn(null);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("label", "SomeLabel");

        mockMvc.perform(put("/api/pets/99/label")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());

        verify(clinicService, never()).savePet(any(Pet.class));
    }

    /**
     * Test that PUT /api/pets/{petId}/label with missing label field returns 400.
     */
    @Test
    public void testUpdatePetLabel_missingLabel_returns400() throws Exception {
        when(clinicService.findPetById(1)).thenReturn(testPet);

        Map<String, String> requestBody = new HashMap<>();
        // no 'label' key — simulate bad request

        mockMvc.perform(put("/api/pets/1/label")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());

        verify(clinicService, never()).savePet(any(Pet.class));
    }
}
