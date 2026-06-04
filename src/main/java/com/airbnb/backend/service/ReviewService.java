package com.airbnb.backend.service;

import com.airbnb.backend.dto.request.ReviewRequestDto;
import com.airbnb.backend.dto.response.ReviewResponseDto;
import com.airbnb.backend.entity.Booking;
import com.airbnb.backend.entity.BookingStatus;
import com.airbnb.backend.entity.Property;
import com.airbnb.backend.entity.Review;
import com.airbnb.backend.entity.User;
import com.airbnb.backend.exception.BadRequestException;
import com.airbnb.backend.exception.ResourceNotFoundException;
import com.airbnb.backend.repository.BookingRepository;
import com.airbnb.backend.repository.PropertyRepository;
import com.airbnb.backend.repository.ReviewRepository;
import com.airbnb.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, 
                         PropertyRepository propertyRepository, 
                         UserRepository userRepository, 
                         BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ReviewResponseDto addReview(ReviewRequestDto requestDto) {
        Property property = propertyRepository.findById(requestDto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        User guest = userRepository.findById(requestDto.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        // Validate if guest has a completed booking for this property
        List<Booking> guestBookings = bookingRepository.findByGuestId(guest.getId());
        boolean hasCompletedBooking = guestBookings.stream()
                .anyMatch(b -> b.getProperty().getId().equals(property.getId()) && b.getStatus() == BookingStatus.COMPLETED);

        if (!hasCompletedBooking) {
            throw new BadRequestException("You can only review properties where you have a completed stay.");
        }

        Review review = new Review();
        review.setProperty(property);
        review.setGuest(guest);
        review.setRating(requestDto.getRating());
        review.setComment(requestDto.getComment());

        Review savedReview = reviewRepository.save(review);
        return mapToDto(savedReview);
    }

    public List<ReviewResponseDto> getPropertyReviews(Long propertyId) {
        return reviewRepository.findByPropertyId(propertyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ReviewResponseDto mapToDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setPropertyId(review.getProperty().getId());
        dto.setGuestId(review.getGuest().getId());
        dto.setGuestName(review.getGuest().getName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
