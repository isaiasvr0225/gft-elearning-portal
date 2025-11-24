package com.gft.infrastructure.repositories;

import com.gft.domain.Month;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthRepository extends JpaRepository<Month, Integer> {
  Optional<Month> findMonthByName(@NotNull @NotBlank String name);
}