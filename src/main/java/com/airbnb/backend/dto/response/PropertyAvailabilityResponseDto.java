package com.airbnb.backend.dto.response;

import java.time.LocalDate;

public class PropertyAvailabilityResponseDto {
    private Long id;
    private Long propertyId;
    private LocalDate availableFrom;
    private LocalDate availableTo;

    public PropertyAvailabilityResponseDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalDate getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(LocalDate availableTo) {
        this.availableTo = availableTo;
    }
}
