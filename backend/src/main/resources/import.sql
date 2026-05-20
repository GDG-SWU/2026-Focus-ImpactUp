
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f1b8c2d3-1111-4444-8888-9999999999aa', 'Cruz Roja - Puerto Los Cristianos (Red Cross Center)', 'hospital', 28.0415, -16.7110, 'available', 1);
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f2b8c2d3-2222-4444-8888-9999999999bb', 'Centro de Salud Los Cristianos (Triage Hospital)', 'hospital', 28.0435, -16.7145, 'crowded', 1);
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f3b8c2d3-3333-4444-8888-9999999999cc', 'UNHCR Temporary Refugee Camp A', 'camp', 28.0405, -16.7125, 'available', 1);
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f4b8c2d3-4444-4444-8888-9999999999dd', 'CEAR Emergency Water Supply Station', 'water', 28.0420, -16.7100, 'available', 1);

INSERT INTO ocr_danger_lexicon (substance_name, target_type, mapping_code) VALUES ('Amoxicillin', 'ALLERGY', 'penicillin');
INSERT INTO ocr_danger_lexicon (substance_name, target_type, mapping_code) VALUES ('Glimepiride', 'CONDITION', 'diabetes');
INSERT INTO ocr_danger_lexicon (substance_name, target_type, mapping_code) VALUES ('Ibuprofen', 'ALLERGY', 'nsaid_allergy');

-- ====================================================================
-- MEDICAL TERMS MULTILINGUAL DATA (5 FIXED LANGUAGES: ar, fr, wo, ma, fu)
-- ====================================================================

INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('diabetes', 'ar', 'السكري');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('diabetes', 'fr', 'Diabète');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('diabetes', 'wo', 'Dafa am sukar');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('diabetes', 'ma', 'Diabetes');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('diabetes', 'fu', 'Diabetes');

INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('asthma', 'ar', 'الربو');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('asthma', 'fr', 'Asthme');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('asthma', 'wo', 'Res-metina');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('asthma', 'ma', 'Asthma');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('asthma', 'fu', 'Asthme');

INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('penicillin', 'ar', 'حساسية البنسلين');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('penicillin', 'fr', 'Allergie à la pénicilline');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('penicillin', 'wo', 'Penicillin');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('penicillin', 'ma', 'Penicillin');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('penicillin', 'fu', 'Penicillin');

INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('nsaid_allergy', 'ar', 'حساسية مضادات الالتهاب غير الستيرoidية');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('nsaid_allergy', 'fr', 'Allergie aux AINS');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('nsaid_allergy', 'wo', 'Nsaid');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('nsaid_allergy', 'ma', 'Nsaid');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('nsaid_allergy', 'fu', 'Nsaid');

INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('hypertension', 'ar', 'ارتفاع ضغط الدم');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('hypertension', 'fr', 'Hypertension');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('hypertension', 'wo', 'Metit mbege');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('hypertension', 'ma', 'Hypertension');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('hypertension', 'fu', 'Hypertension');

INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('sulfonamide', 'ar', 'حساسية السلفوناميد');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('sulfonamide', 'fr', 'Allergie aux sulfonamides');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('sulfonamide', 'wo', 'Sulfonamide');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('sulfonamide', 'ma', 'Sulfonamide');
INSERT INTO medical_terms (term_code, lang_code, translated_text) VALUES ('sulfonamide', 'fu', 'Sulfonamide');
