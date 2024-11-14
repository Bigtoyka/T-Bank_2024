package org.tbank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tbank.dao.UniversalDAO;
import org.tbank.models.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;


class LocationServiceTest {
    @Mock
    private UniversalDAO<String, Location> locationDAO;

    @InjectMocks
    private LocationService locationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void getAllLocation() {
        Location location1 = new Location(null,"slug1", "Location1", new ArrayList<>());
        Location location2 = new Location(null, "slug2", "Location2", new ArrayList<>());
        List<Location> locations = Arrays.asList(location1, location2);

        when(locationDAO.getAll()).thenReturn(locations);
        Collection<Location> result = locationService.getAllLocation();

        assertEquals(locations, result);
    }

    @Test
    void getLocation() {
        Location location = new Location(null, "slug1", "Category1", new ArrayList<>());
        when(locationDAO.get("slug1")).thenReturn(location);

        Location result = locationService.getLocation("slug1");

        assertEquals(location, result);
    }

    @Test
    public void getLocation_NotFound_shouldThrowException() {
        when(locationDAO.get("slug")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> locationService.getLocation("slug"));
    }

    @Test
    void addLocation() {
        Location location = new Location(null, "slug2", "Location2",new ArrayList<>());

        locationService.addLocation("slug2", location);

        verify(locationDAO, times(1)).put("slug2", location);

    }

    @Test
    public void addLocation_AlreadyExists_shouldThrowException() {
        Location location = new Location(null, "slug2", "Category2",new ArrayList<>());
        when(locationDAO.get("slug2")).thenReturn(location);

        assertThrows(IllegalArgumentException.class, () -> locationService.addLocation("slug2", location));
    }

    @Test
    void updateLocation() {
        Location location = new Location(null, "slug1", "Category1", new ArrayList<>());

        locationService.updateLocation(location.getSlug(), location);

        verify(locationDAO).update(location.getSlug(), location);
    }

    @Test
    void deleteCategory() {
        Location location = new Location(null, "slug2", "Category2", new ArrayList<>());

        when(locationDAO.get("slug2")).thenReturn(location);
        locationService.deleteCategory("slug2");

        verify(locationDAO, times(1)).remove("slug2");
    }

    @Test
    public void deleteCategory_NotFoundCategory_shouldThrowException() {
        when(locationDAO.get("slug")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> locationService.deleteCategory("slug"));
    }
}