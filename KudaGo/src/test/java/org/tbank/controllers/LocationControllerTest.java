package org.tbank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.models.Location;
import org.tbank.repository.LocationRepository;
import org.tbank.service.LocationService;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class LocationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;
    @MockBean
    private LocationRepository locationRepository;

    private Location location1;

    @BeforeEach
    void setUp() {
        location1 = new Location(null, "slug1", "Category1", new ArrayList<>());
    }

    @Test
    void getLocation_SuccessGetLocation_shouldReturnLocationById() throws Exception {
        Mockito.when(locationRepository.findBySlug(anyString())).thenReturn(location1);

        mockMvc.perform(get("/api/v1/locations/slug"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(location1.getSlug()))
                .andExpect(jsonPath("$.name").value(location1.getName()));
    }

    @Test
    void addLocation_SuccessAddLocation_shouldAddLocation() throws Exception {
        Mockito.doNothing().when(locationService).addLocation(anyString(), any(Location.class));
        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(location1)))
                .andExpect(status().isOk())
                .andExpect(content().string("Локация добавлена"));
    }

    @Test
    void updateLocation_SuccessUpdateLocation_shouldUpdateLocation() throws Exception {
        Mockito.when(locationRepository.findBySlug(anyString())).thenReturn(location1);
        mockMvc.perform(put("/api/v1/locations/slug")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(location1)))
                .andExpect(status().isOk())
                .andExpect(content().string("Обновление прошло успешно"));
    }

    @Test
    void deleteLocation_SuccessDeleteLocation_shouldDeleteLocation() throws Exception {
        Mockito.when(locationRepository.findBySlug(anyString())).thenReturn(location1);
        mockMvc.perform(delete("/api/v1/locations/slug"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLocation_SlugNotFound_shouldThrowException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException()).when(locationRepository).findBySlug(anyString());

        mockMvc.perform(delete("/api/v1/locations/slug"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategory_SlugNotFound_shouldThrowException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException()).when(locationRepository).findBySlug(anyString());

        mockMvc.perform(get("/api/v1/locations/slug"))
                .andExpect(status().isBadRequest());
    }
}