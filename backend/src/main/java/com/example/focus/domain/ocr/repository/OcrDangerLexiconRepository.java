package com.example.focus.domain.ocr.repository;

import com.example.focus.domain.ocr.entity.OcrDangerLexicon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OcrDangerLexiconRepository extends JpaRepository<OcrDangerLexicon, Long> {

    @Query("SELECT o FROM OcrDangerLexicon o WHERE o.mappingCode IN :mappingCodes")
    List<OcrDangerLexicon> findByMappingCodeIn(List<String> mappingCodes);
}
