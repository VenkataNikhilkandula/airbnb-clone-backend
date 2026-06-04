package com.airbnb.backend.service;

import com.airbnb.backend.dto.request.BookingRequestDto;
import com.airbnb.backend.dto.response.BookingResponseDto;
import com.airbnb.backend.entity.*;
import com.airbnb.backend.exception.BadRequestException;
import com.airbnb.backend.exception.DoubleBookingException;
import com.airbnb.backend.exception.ResourceNotFoundException;
import com.airbnb.backend.repository.BookingRepository;
import com.airbnb.backend.repository.PropertyAvailabilityRepository;
import com.airbnb.backend.repository.PropertyRepository;
import com.airbnb.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyAvailabilityRepository availabilityRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, 
                          PropertyRepository propertyRepository, 
                          UserRepository userRepository, 
                          PropertyAvailabilityRepository availabilityRepository) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto requestDto) {
        if (requestDto.getStartDate().isAfter(requestDto.getEndDate()) || requestDto.getStartDate().isEqual(requestDto.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }

        Property property = propertyRepository.findById(requestDto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        User guest = userRepository.findById(requestDto.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        if (guest.getRole() != Role.GUEST) {
            throw new BadRequestException("Only GUEST can make a booking");
        }

        // Check if property is available in these dates based on host settings
        List<PropertyAvailability> availabilities = availabilityRepository.findByPropertyId(property.getId());
        boolean isAvailable = availabilities.stream().anyMatch(av ->
                (requestDto.getStartDate().isEqual(av.getAvailableFrom()) || requestDto.getStartDate().isAfter(av.getAvailableFrom())) &&
                (requestDto.getEndDate().isEqual(av.getAvailableTo()) || requestDto.getEndDate().isBefore(av.getAvailableTo()))
        );

        if (!isAvailable) {
            throw new BadRequestException("Property is not available for the selected dates based on host settings.");
        }

        // Prevent Double Booking
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(property.getId(), requestDto.getStartDate(), requestDto.getEndDate());
        if (!overlappingBookings.isEmpty()) {
            throw new DoubleBookingException("Property is already booked for these dates.");
        }

        // Calculate Price
        long daysBetween = ChronoUnit.DAYS.between(requestDto.getStartDate(), requestDto.getEndDate());
        BigDecimal totalPrice = property.getPricePerNight().multiply(BigDecimal.valueOf(daysBetween));

        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setStartDate(requestDto.getStartDate());
        booking.setEndDate(requestDto.getEndDate());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.REQUESTED); // Or CONFIRMED depending on business logic

        Booking savedBooking = bookingRepository.save(booking);
        return mapToDto(savedBooking);
    }

    @Transactional
    public BookingResponseDto cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        return mapToDto(updatedBooking);
    }

    public List<BookingResponseDto> getUserBookings(Long userId) {
        // Find if user is host or guest
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        List<Booking> bookings;
        if (user.getRole() == Role.GUEST) {
            bookings = bookingRepository.findByGuestId(userId);
        } else {
            throw new BadRequestException("Use proper endpoint for Host to view property bookings");
        }
        
        return bookings.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<BookingResponseDto> getPropertyBookings(Long propertyId) {
        return bookingRepository.findByPropertyId(propertyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private BookingResponseDto mapToDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setPropertyId(booking.getProperty().getId());
        dto.setPropertyTitle(booking.getProperty().getTitle());
        dto.setGuestId(booking.getGuest().getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}
