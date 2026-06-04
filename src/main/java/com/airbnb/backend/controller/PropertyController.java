package com.airbnb.backend.controller;

import com.airbnb.backend.dto.request.PropertyAvailabilityRequestDto;
import com.airbnb.backend.dto.request.PropertyRequestDto;
import com.airbnb.backend.dto.response.PropertyAvailabilityResponseDto;
import com.airbnb.backend.dto.response.PropertyResponseDto;
import com.airbnb.backend.dto.response.PropertyStatsResponseDto;
import com.airbnb.backend.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@Tag(name = "Property Management", description = "Endpoints for managing properties")
public class PropertyController {

    private final PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    @Operation(summary = "Create a new property listing")
    public ResponseEntity<PropertyResponseDto> createProperty(@Valid @RequestBody PropertyRequestDto requestDto) {
        return new ResponseEntity<>(propertyService.createProperty(requestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update property details")
    public ResponseEntity<PropertyResponseDto> updateProperty(@PathVariable Long id, @Valid @RequestBody PropertyRequestDto requestDto) {
        return ResponseEntity.ok(propertyService.updateProperty(id, requestDto));
    }

    @GetMapping
    @Operation(summary = "Get all properties")
    public ResponseEntity<List<PropertyResponseDto>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property details by ID")
    public ResponseEntity<PropertyResponseDto> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @PostMapping("/{id}/availability")
    @Operation(summary = "Set property availability dates")
    public ResponseEntity<PropertyAvailabilityResponseDto> setAvailability(@PathVariable Long id, @Valid @RequestBody PropertyAvailabilityRequestDto requestDto) {
        return new ResponseEntity<>(propertyService.setAvailability(id, requestDto), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}/statistics")
    @Operation(summary = "Get property booking statistics")
    public ResponseEntity<PropertyStatsResponseDto> getPropertyStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyStatistics(id));
    }
}
