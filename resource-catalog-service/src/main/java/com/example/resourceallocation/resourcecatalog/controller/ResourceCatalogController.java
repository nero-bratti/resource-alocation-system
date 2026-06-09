package com.example.resourceallocation.resourcecatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resource-catalog")
@Tag(name = "Resource Catalog", description = "Manage rooms, labs, and equipment")
public class ResourceCatalogController {

    @GetMapping("/resources")
    @Operation(summary = "List available resources")
    public ResponseEntity<List<String>> listResources() {
        return ResponseEntity.ok(List.of("Room A", "Computer Lab 1", "AV Cart 3"));
    }
}
