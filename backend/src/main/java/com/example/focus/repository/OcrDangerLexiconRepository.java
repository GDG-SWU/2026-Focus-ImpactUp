package com.example.focus.repository;

import com.example.focus.domain.OcrDangerLexicon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OcrDangerLexiconRepository extends JpaRepository<OcrDangerLexicon, Long> {

    @Query("SELECT o FROM OcrDangerLexicon o WHERE o.mappingCode IN :mappingCodes")
    List<OcrDangerLexicon> findByMappingCodes(@Param("mappingCodes") List<String> mappingCodes);
}
