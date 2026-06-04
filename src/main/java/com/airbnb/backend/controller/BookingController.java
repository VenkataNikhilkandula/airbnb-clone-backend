package com.airbnb.backend.controller;

import com.airbnb.backend.dto.request.BookingRequestDto;
import com.airbnb.backend.dto.response.BookingResponseDto;
import com.airbnb.backend.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Management", description = "Endpoints for managing bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Create a booking request")
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto requestDto) {
        return new ResponseEntity<>(bookingService.createBooking(requestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<BookingResponseDto> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user bookings")
    public ResponseEntity<List<BookingResponseDto>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }
    
    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Get property bookings")
    public ResponseEntity<List<BookingResponseDto>> getPropertyBookings(@PathVariable Long propertyId) {
        return ResponseEntity.ok(bookingService.getPropertyBookings(propertyId));
    }
}
