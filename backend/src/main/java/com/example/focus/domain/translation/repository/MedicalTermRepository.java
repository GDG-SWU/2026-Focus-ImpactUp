package com.example.focus.domain.translation.repository;

import com.example.focus.domain.translation.entity.MedicalTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalTermRepository extends JpaRepository<MedicalTerm, Long> {

    List<MedicalTerm> findByTermCode(String termCode);
}