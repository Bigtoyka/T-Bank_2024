package org.tbank.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.models.Event;
import org.tbank.models.Location;
import org.tbank.repository.EventRepository;
import org.tbank.repository.LocationRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        locationRepository.deleteAll();
    }

    @Test
    public void CreateEvent() throws Exception {
        Location location = new Location(null, "slug-test", "Test Location", null);
        Location savedLocation = locationRepository.save(location);

        String eventJson = String.format("""
            {
                "title": "Test Event",
                "startDate": "%s",
                "price": "100",
                "location": {
                    "id": %d
                }
            }
        """, LocalDateTime.now().toString(), savedLocation.getId());

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isCreated());

        Event savedEvent = eventRepository.findAll().get(0);
        assertThat(savedEvent).isNotNull();
        assertThat(savedEvent.getTitle()).isEqualTo("Test Event");
        assertThat(savedEvent.getLocation().getId()).isEqualTo(savedLocation.getId());
    }

    @Test
    public void UpdateEvent() throws Exception {
        Location location = new Location(null, "slug-test", "Test Location", null);
        Location savedLocation = locationRepository.save(location);

        Event event = new Event();
        event.setTitle("Old Event");
        event.setPrice("50");
        event.setStartDate(LocalDateTime.now());
        event.setLocation(savedLocation);
        Event savedEvent = eventRepository.save(event);

        String updateJson = String.format("""
            {
                "id": %d,
                "title": "Updated Event",
                "startDate": "%s",
                "price": "150",
                "location": {
                    "id": %d
                }
            }
        """, savedEvent.getId(), LocalDateTime.now().toString(), savedLocation.getId());

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isCreated());

        Event updatedEvent = eventRepository.findById(savedEvent.getId()).orElse(null);
        assertThat(updatedEvent).isNotNull();
        assertThat(updatedEvent.getTitle()).isEqualTo("Updated Event");
        assertThat(updatedEvent.getPrice()).isEqualTo("150");
    }
}
