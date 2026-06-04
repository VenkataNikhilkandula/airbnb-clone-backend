package com.airbnb.backend.controller;

import com.airbnb.backend.dto.request.ReviewRequestDto;
import com.airbnb.backend.dto.response.ReviewResponseDto;
import com.airbnb.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Management", description = "Endpoints for property reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Add a property review")
    public ResponseEntity<ReviewResponseDto> addReview(@Valid @RequestBody ReviewRequestDto requestDto) {
        return new ResponseEntity<>(reviewService.addReview(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Get all reviews for a property")
    public ResponseEntity<List<ReviewResponseDto>> getPropertyReviews(@PathVariable Long propertyId) {
        return ResponseEntity.ok(reviewService.getPropertyReviews(propertyId));
    }
}
