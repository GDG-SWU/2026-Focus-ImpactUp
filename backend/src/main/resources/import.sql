
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f1b8c2d3-1111-4444-8888-9999999999aa', 'Cruz Roja - Puerto Los Cristianos (Red Cross Center)', 'hospital', 28.0415, -16.7110, 'available', 1);
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f2b8c2d3-2222-4444-8888-9999999999bb', 'Centro de Salud Los Cristianos (Triage Hospital)', 'hospital', 28.0435, -16.7145, 'crowded', 1);
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f3b8c2d3-3333-4444-8888-9999999999cc', 'UNHCR Temporary Refugee Camp A', 'camp', 28.0405, -16.7125, 'available', 1);
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f4b8c2d3-4444-4444-8888-9999999999dd', 'CEAR Emergency Water Supply Station', 'water', 28.0420, -16.7100, 'available', 1);

INSERT INTO ocr_danger_lexicon (substance_name, target_type, mapping_code) VALUES ('Amoxicillin', 'ALLERGY', 'penicillin');
INSERT INTO ocr_danger_lexicon (substance_name, target_type, mapping_code) VALUES ('Glimepiride', 'CONDITION', 'diabetes');
INSERT INTO ocr_danger_lexicon (substance_name, target_type, mapping_code) VALUES ('Ibuprofen', 'ALLERGY', 'nsaid_allergy');