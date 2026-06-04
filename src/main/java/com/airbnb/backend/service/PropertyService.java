package com.airbnb.backend.service;

import com.airbnb.backend.dto.request.PropertyAvailabilityRequestDto;
import com.airbnb.backend.dto.request.PropertyRequestDto;
import com.airbnb.backend.dto.response.PropertyAvailabilityResponseDto;
import com.airbnb.backend.dto.response.PropertyResponseDto;
import com.airbnb.backend.dto.response.PropertyStatsResponseDto;
import com.airbnb.backend.entity.Booking;
import com.airbnb.backend.entity.Property;
import com.airbnb.backend.entity.PropertyAvailability;
import com.airbnb.backend.entity.User;
import com.airbnb.backend.entity.Role;
import com.airbnb.backend.entity.BookingStatus;
import com.airbnb.backend.exception.BadRequestException;
import com.airbnb.backend.exception.ResourceNotFoundException;
import com.airbnb.backend.repository.BookingRepository;
import com.airbnb.backend.repository.PropertyAvailabilityRepository;
import com.airbnb.backend.repository.PropertyRepository;
import com.airbnb.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyAvailabilityRepository availabilityRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository, 
                           UserRepository userRepository, 
                           PropertyAvailabilityRepository availabilityRepository, 
                           BookingRepository bookingRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
        this.bookingRepository = bookingRepository;
    }

    public PropertyResponseDto createProperty(PropertyRequestDto requestDto) {
        User host = userRepository.findById(requestDto.getHostId())
                .orElseThrow(() -> new ResourceNotFoundException("Host not found"));

        if (host.getRole() != Role.HOST) {
            throw new BadRequestException("User must be a HOST to create a property");
        }

        Property property = new Property();
        property.setTitle(requestDto.getTitle());
        property.setDescription(requestDto.getDescription());
        property.setLocation(requestDto.getLocation());
        property.setPricePerNight(requestDto.getPricePerNight());
        property.setHost(host);

        Property savedProperty = propertyRepository.save(property);
        return mapToDto(savedProperty);
    }

    public PropertyResponseDto updateProperty(Long id, PropertyRequestDto requestDto) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        property.setTitle(requestDto.getTitle());
        property.setDescription(requestDto.getDescription());
        property.setLocation(requestDto.getLocation());
        property.setPricePerNight(requestDto.getPricePerNight());

        Property updatedProperty = propertyRepository.save(property);
        return mapToDto(updatedProperty);
    }

    public List<PropertyResponseDto> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PropertyResponseDto getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        return mapToDto(property);
    }

    public PropertyAvailabilityResponseDto setAvailability(Long propertyId, PropertyAvailabilityRequestDto requestDto) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        PropertyAvailability availability = new PropertyAvailability();
        availability.setProperty(property);
        availability.setAvailableFrom(requestDto.getAvailableFrom());
        availability.setAvailableTo(requestDto.getAvailableTo());

        PropertyAvailability savedAvailability = availabilityRepository.save(availability);

        PropertyAvailabilityResponseDto dto = new PropertyAvailabilityResponseDto();
        dto.setId(savedAvailability.getId());
        dto.setPropertyId(savedAvailability.getProperty().getId());
        dto.setAvailableFrom(savedAvailability.getAvailableFrom());
        dto.setAvailableTo(savedAvailability.getAvailableTo());
        return dto;
    }
    
    public PropertyStatsResponseDto getPropertyStatistics(Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new ResourceNotFoundException("Property not found");
        }
        
        List<Booking> bookings = bookingRepository.findByPropertyId(propertyId);
        
        long total = bookings.size();
        long confirmed = bookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        long cancelled = bookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
        long completed = bookings.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED).count();
        
        PropertyStatsResponseDto dto = new PropertyStatsResponseDto();
        dto.setPropertyId(propertyId);
        dto.setTotalBookings(total);
        dto.setConfirmedBookings(confirmed);
        dto.setCancelledBookings(cancelled);
        dto.setCompletedBookings(completed);
        return dto;
    }

    private PropertyResponseDto mapToDto(Property property) {
        PropertyResponseDto dto = new PropertyResponseDto();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setLocation(property.getLocation());
        dto.setPricePerNight(property.getPricePerNight());
        dto.setHostId(property.getHost().getId());
        return dto;
    }
}
