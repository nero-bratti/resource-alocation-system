package com.example.resourceallocation.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Service", description = "Create and review room allocations")
public class BookingController {

    @GetMapping
    @Operation(summary = "List active bookings")
    public ResponseEntity<List<String>> listBookings() {
        return ResponseEntity.ok(List.of("Booking 101", "Booking 102"));
    }
}
