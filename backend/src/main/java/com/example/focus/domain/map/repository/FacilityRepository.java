package com.example.focus.domain.map.repository;

import com.example.focus.domain.map.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String> {

    List<Facility> findByCategory(String category);
}