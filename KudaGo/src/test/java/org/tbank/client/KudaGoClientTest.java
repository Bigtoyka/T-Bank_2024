package org.tbank.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class KudaGoClientTest {
    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withMappingFromResource("locations.json")
            .withMappingFromResource("categories.json");

    @Autowired
    private KudaGoClient kudaGoClient;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("https://kudago.com/public-api/v1.4", wireMockContainer::getBaseUrl);
    }


    @Test
    @DisplayName("Возвращение locations")
    @WithMockUser
    public void requestLocations_locationsIsNotEmpty_shouldBeNotEmpty() {
        var locations = kudaGoClient.requestLocation();
        assertThat(locations)
                .isPresent()
                .hasValueSatisfying(array ->
                        assertThat(array)
                                .isNotEmpty()
                                .anySatisfy(location ->
                                        assertThat(location.getSlug()).isEqualTo("msk")
                                )
                );
    }

    @Test
    @DisplayName("Возвращение categories")
    public void requestCategories_CategoriesIsNotEmpty_shouldBeNotEmpty() {
        var categories = kudaGoClient.requestCategories();
        assertThat(categories).isNotEmpty();
    }

}