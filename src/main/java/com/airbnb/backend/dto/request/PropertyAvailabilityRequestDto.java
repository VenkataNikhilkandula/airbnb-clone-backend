package com.airbnb.backend.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class PropertyAvailabilityRequestDto {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Available from date is required")
    @FutureOrPresent(message = "Available from date must be present or future")
    private LocalDate availableFrom;

    @NotNull(message = "Available to date is required")
    @FutureOrPresent(message = "Available to date must be present or future")
    private LocalDate availableTo;

    public PropertyAvailabilityRequestDto() {}

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
