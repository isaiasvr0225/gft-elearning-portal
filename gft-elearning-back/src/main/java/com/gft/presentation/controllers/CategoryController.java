package com.gft.presentation.controllers;

import com.gft.infrastructure.dto.course.CategoryDTO;
import com.gft.infrastructure.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> listAll() {
        var categories = categoryRepository.findAll().stream()
                .map(c -> new CategoryDTO(c.getId(), c.getName()))
                .toList();
        return ResponseEntity.ok(categories);
    }
}
