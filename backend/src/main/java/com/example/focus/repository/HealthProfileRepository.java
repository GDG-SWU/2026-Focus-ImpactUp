package com.example.focus.repository;

import com.example.focus.domain.HealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {
}