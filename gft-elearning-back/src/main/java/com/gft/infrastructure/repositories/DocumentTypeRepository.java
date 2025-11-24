package com.gft.infrastructure.repositories;

import com.gft.domain.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Integer> {

    Optional<DocumentType> findDocumentTypeByName(String name);
}