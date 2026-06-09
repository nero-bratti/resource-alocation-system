package com.example.resourceallocation.permission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permission Service", description = "Verify who can use which resource")
public class PermissionController {

    @GetMapping
    @Operation(summary = "List permission grants")
    public ResponseEntity<List<String>> listPermissions() {
        return ResponseEntity.ok(List.of("Teacher A -> Room A", "Student B -> Computer Lab 1"));
    }
}
