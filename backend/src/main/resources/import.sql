-- ⚠️ ddl-auto=create 상태에서 서버가 켜질 때 자동으로 실행되는 난민 구호소 실전 데이터 세트
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f1b8c2d3-1111-4444-8888-9999999999aa', 'Cruz Roja - Puerto Los Cristianos (Red Cross Center)', 'hospital', 28.0515, -16.7160, 'available', 1);
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f2b8c2d3-2222-4444-8888-9999999999bb', 'Centro de Salud Los Cristianos (Triage Hospital)', 'hospital', 28.0545, -16.7135, 'crowded', 1);
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f3b8c2d3-3333-4444-8888-9999999999cc', 'UNHCR Temporary Refugee Camp A', 'camp', 28.0505, -16.7175, 'available', 1);
INSERT INTO facilities (id, name, category, latitude, longitude, availability, operating) VALUES ('f4b8c2d3-4444-4444-8888-9999999999dd', 'CEAR Emergency Water Supply Station', 'water', 28.0520, -16.7150, 'available', 1);

INSERT INTO ocr_danger_lexicon (substance_name, target_type, mapping_code) VALUES ('Amoxicillin', 'ALLERGY', 'penicillin');
INSERT INTO ocr_danger_lexicon (substance_name, target_type, mapping_code) VALUES ('Glimepiride', 'CONDITION', 'diabetes');
INSERT INTO ocr_danger_lexicon (substance_name, target_type, mapping_code) VALUES ('Ibuprofen', 'ALLERGY', 'nsaid_allergy');