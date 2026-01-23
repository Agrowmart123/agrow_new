package com.agrowmart.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.agrowmart.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByName(String name);
    
    //Added By Aakanksha - 19/01/2026
    List<Role> findByNameIn(List<String> names);
}