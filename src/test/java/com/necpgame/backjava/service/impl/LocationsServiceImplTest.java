package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.CityEntity;
import com.necpgame.backjava.model.City;
import com.necpgame.backjava.model.GetCities200Response;
import com.necpgame.backjava.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit тест для LocationsServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class LocationsServiceImplTest {
    
    @Mock
    private CityRepository cityRepository;
    
    @InjectMocks
    private LocationsServiceImpl locationsService;
    
    private CityEntity nightCity;
    private CityEntity neoTokyo;
    
    @BeforeEach
    void setUp() {
        nightCity = new CityEntity();
        nightCity.setId(UUID.randomUUID());
        nightCity.setName("Night City");
        nightCity.setRegion("US");
        nightCity.setDescription("Город будущего");
        
        neoTokyo = new CityEntity();
        neoTokyo.setId(UUID.randomUUID());
        neoTokyo.setName("Neo-Tokyo");
        neoTokyo.setRegion("ASIA");
        neoTokyo.setDescription("Неоновый мегаполис");
    }
    
    @Test
    void getCities_shouldReturnAllCities() {
        // Arrange
        when(cityRepository.findAll()).thenReturn(Arrays.asList(nightCity, neoTokyo));
        
        // Act
        GetCities200Response response = locationsService.getCities(null, null);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getCities());
        assertEquals(2, response.getCities().size());
        
        City city1 = response.getCities().get(0);
        assertEquals("Night City", city1.getName());
        assertEquals("US", city1.getRegion());
        assertEquals("Город будущего", city1.getDescription());
        
        verify(cityRepository, times(1)).findAll();
    }
    
    @Test
    void getCities_shouldHandleEmptyList() {
        // Arrange
        when(cityRepository.findAll()).thenReturn(List.of());
        
        // Act
        GetCities200Response response = locationsService.getCities(null, null);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getCities());
        assertTrue(response.getCities().isEmpty());
    }
}

