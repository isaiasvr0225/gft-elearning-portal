package com.gft.infrastructure.repositories;

import com.gft.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {


    Optional<Role> findRoleById(Integer id);

    Optional<Role> findRoleByName(String name);
}