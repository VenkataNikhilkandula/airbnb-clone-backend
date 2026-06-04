package com.airbnb.backend.repository;

import com.airbnb.backend.entity.Booking;
import com.airbnb.backend.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByGuestId(Long guestId);

    List<Booking> findByPropertyId(Long propertyId);

    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId " +
           "AND b.status IN ('REQUESTED', 'CONFIRMED') " +
           "AND ((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<Booking> findOverlappingBookings(@Param("propertyId") Long propertyId, 
                                          @Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
}
