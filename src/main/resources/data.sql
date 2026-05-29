-- =========================================
-- Stocks (100개) - Variant 1:1 매칭
-- variant_id = 1~100 (위 INSERT 순서대로)
-- product_id는 위 더미데이터 기준
-- available_quantity 제거 버전
-- =========================================

-- Product 1: 베이직 코튼 티셔츠 (variant 1~8) - 인기 상품, 재고 많음
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (1, 1, 150, 12),
                                                                                   (2, 1, 200, 25),
                                                                                   (3, 1, 180, 18),
                                                                                   (4, 1, 150, 10),
                                                                                   (5, 1, 200, 30),
                                                                                   (6, 1, 180, 22),
                                                                                   (7, 1, 120, 8),
                                                                                   (8, 1, 100, 5);

-- Product 2: 오버핏 코튼 티셔츠 (variant 9~14)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (9, 2, 120, 15),
                                                                                   (10, 2, 150, 20),
                                                                                   (11, 2, 120, 18),
                                                                                   (12, 2, 150, 22),
                                                                                   (13, 2, 80, 5),
                                                                                   (14, 2, 60, 3);

-- Product 3: 슬림핏 코튼 티셔츠 (variant 15~19)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (15, 3, 100, 8),
                                                                                   (16, 3, 130, 15),
                                                                                   (17, 3, 110, 10),
                                                                                   (18, 3, 130, 18),
                                                                                   (19, 3, 80, 6);

-- Product 4: 프리미엄 린넨 셔츠 (variant 20~25)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (20, 4, 80, 8),
                                                                                   (21, 4, 90, 12),
                                                                                   (22, 4, 70, 5),
                                                                                   (23, 4, 85, 10),
                                                                                   (24, 4, 50, 3),
                                                                                   (25, 4, 40, 2);

-- Product 5: 옥스포드 코튼 셔츠 (variant 26~30)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (26, 5, 100, 10),
                                                                                   (27, 5, 120, 15),
                                                                                   (28, 5, 90, 8),
                                                                                   (29, 5, 100, 12),
                                                                                   (30, 5, 60, 4);

-- Product 6: 체크 플란넬 셔츠 (variant 31~34) - 시즌 상품
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (31, 6, 70, 5),
                                                                                   (32, 6, 80, 8),
                                                                                   (33, 6, 65, 4),
                                                                                   (34, 6, 50, 3);

-- Product 7: 스트레이트 데님 팬츠 (variant 35~39)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (35, 7, 90, 10),
                                                                                   (36, 7, 120, 18),
                                                                                   (37, 7, 100, 12),
                                                                                   (38, 7, 60, 5),
                                                                                   (39, 7, 70, 6);

-- Product 8: 슬림 데님 팬츠 (variant 40~44)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (40, 8, 100, 12),
                                                                                   (41, 8, 130, 20),
                                                                                   (42, 8, 110, 15),
                                                                                   (43, 8, 90, 10),
                                                                                   (44, 8, 110, 14);

-- Product 9: 와이드 데님 팬츠 (variant 45~48)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (45, 9, 80, 8),
                                                                                   (46, 9, 90, 12),
                                                                                   (47, 9, 100, 15),
                                                                                   (48, 9, 60, 5);

-- Product 10: 코튼 치노 팬츠 (variant 49~53)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (49, 10, 90, 8),
                                                                                   (50, 10, 110, 12),
                                                                                   (51, 10, 80, 6),
                                                                                   (52, 10, 100, 10),
                                                                                   (53, 10, 70, 4);

-- Product 11: 울 블렌드 코트 (variant 54~57) - 고가 상품, 재고 적음
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (54, 11, 40, 3),
                                                                                   (55, 11, 50, 5),
                                                                                   (56, 11, 45, 4),
                                                                                   (57, 11, 35, 2);

-- Product 12: 캐시미어 블렌드 코트 (variant 58~60) - 프리미엄, 재고 적음
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (58, 12, 25, 2),
                                                                                   (59, 12, 30, 3),
                                                                                   (60, 12, 20, 1);

-- Product 13: 패딩 다운 자켓 (variant 61~65)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (61, 13, 60, 8),
                                                                                   (62, 13, 80, 12),
                                                                                   (63, 13, 50, 5),
                                                                                   (64, 13, 40, 3),
                                                                                   (65, 13, 55, 6);

-- Product 14: 레더 바이커 자켓 (variant 66~68) - 프리미엄
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (66, 14, 20, 2),
                                                                                   (67, 14, 25, 3),
                                                                                   (68, 14, 15, 1);

-- Product 15: 캐주얼 후드 집업 (variant 69~74)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (69, 15, 100, 12),
                                                                                   (70, 15, 130, 18),
                                                                                   (71, 15, 110, 15),
                                                                                   (72, 15, 140, 22),
                                                                                   (73, 15, 70, 8),
                                                                                   (74, 15, 50, 4);

-- Product 16: 베이직 크루넥 니트 (variant 75~79)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (75, 16, 80, 8),
                                                                                   (76, 16, 100, 12),
                                                                                   (77, 16, 70, 6),
                                                                                   (78, 16, 90, 10),
                                                                                   (79, 16, 60, 5);

-- Product 17: 터틀넥 캐시미어 니트 (variant 80~83) - UNAVAILABLE 상품
-- 비즈니스적으로 0이거나 매우 적게 설정
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (80, 17, 0, 0),
                                                                                   (81, 17, 0, 0),
                                                                                   (82, 17, 5, 0),
                                                                                   (83, 17, 0, 0);

-- Product 18: 러닝화 에어맥스 (variant 84~89)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (84, 18, 80, 10),
                                                                                   (85, 18, 120, 18),
                                                                                   (86, 18, 100, 15),
                                                                                   (87, 18, 130, 22),
                                                                                   (88, 18, 90, 12),
                                                                                   (89, 18, 60, 6);

-- Product 19: 캔버스 스니커즈 (variant 90~95)
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (90, 19, 100, 12),
                                                                                   (91, 19, 130, 20),
                                                                                   (92, 19, 110, 16),
                                                                                   (93, 19, 90, 10),
                                                                                   (94, 19, 110, 14),
                                                                                   (95, 19, 70, 6);

-- Product 20: 구버전 한정판 운동화 (variant 96~100) - DELETED, 재고 0
INSERT INTO stocks (variant_id, product_id, total_quantity, reserved_quantity) VALUES
                                                                                   (96, 20, 0, 0),
                                                                                   (97, 20, 0, 0),
                                                                                   (98, 20, 0, 0),
                                                                                   (99, 20, 0, 0),
                                                                                   (100, 20, 0, 0);
