package com.gft.infrastructure.repositories;

import com.gft.domain.City;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {

    Optional<City> findCityByName(@NotNull @NotBlank String name);

    City findCityById(Integer id);
}