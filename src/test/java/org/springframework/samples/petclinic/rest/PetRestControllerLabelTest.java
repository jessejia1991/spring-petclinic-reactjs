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
import org.springframework.test.web.servlet.MvcResult;

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
 * Tests for the pet label endpoint in PetRestController.
 * Verifies that GET /api/pets/{petId}/label returns the current label,
 * and PUT /api/pets/{petId}/label persists the new label correctly.
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
        testPet.setLabel("Friendly");
    }

    @Test
    public void testGetPetLabel_returnsLabelSuccessfully() throws Exception {
        when(clinicService.findPetById(1)).thenReturn(testPet);

        mockMvc.perform(get("/api/pets/1/label")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("Friendly")))
                .andExpect(jsonPath("$.petId", is(1)));

        verify(clinicService, times(1)).findPetById(1);
    }

    @Test
    public void testGetPetLabel_petNotFound_returns404() throws Exception {
        when(clinicService.findPetById(99)).thenReturn(null);

        mockMvc.perform(get("/api/pets/99/label")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPetLabel_noLabelSet_returnsEmptyOrNull() throws Exception {
        Pet petWithoutLabel = new Pet();
        petWithoutLabel.setId(2);
        petWithoutLabel.setName("Max");
        petWithoutLabel.setLabel(null);

        when(clinicService.findPetById(2)).thenReturn(petWithoutLabel);

        mockMvc.perform(get("/api/pets/2/label")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petId", is(2)));
    }

    @Test
    public void testPutPetLabel_persistsLabelCorrectly() throws Exception {
        when(clinicService.findPetById(1)).thenReturn(testPet);
        doNothing().when(clinicService).savePet(any(Pet.class));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("label", "Energetic");

        mockMvc.perform(put("/api/pets/1/label")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("Energetic")))
                .andExpect(jsonPath("$.petId", is(1)));

        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
        verify(clinicService, times(1)).savePet(petCaptor.capture());
        Pet savedPet = petCaptor.getValue();
        assertEquals("Energetic", savedPet.getLabel());
        assertEquals(Integer.valueOf(1), savedPet.getId());
    }

    @Test
    public void testPutPetLabel_petNotFound_returns404() throws Exception {
        when(clinicService.findPetById(99)).thenReturn(null);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("label", "Energetic");

        mockMvc.perform(put("/api/pets/99/label")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());

        verify(clinicService, never()).savePet(any(Pet.class));
    }

    @Test
    public void testPutPetLabel_emptyLabel_persistsEmptyLabel() throws Exception {
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

    @Test
    public void testPutPetLabel_updatesExistingLabel() throws Exception {
        testPet.setLabel("Old Label");
        when(clinicService.findPetById(1)).thenReturn(testPet);
        doNothing().when(clinicService).savePet(any(Pet.class));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("label", "New Label");

        mockMvc.perform(put("/api/pets/1/label")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("New Label")));

        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
        verify(clinicService, times(1)).savePet(petCaptor.capture());
        assertEquals("New Label", petCaptor.getValue().getLabel());
    }
}
