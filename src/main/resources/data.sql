-- data.sql

-- Member 데이터 (기존 데이터가 없다면)
-- Member 데이터 (Email 임베디드 타입 고려)
INSERT INTO member (id, provider, provider_id, email, nickname, member_status, registered_at, deactivated_at)
VALUES (1, 'KAKAO', 'kakao_12345', 'test1@test.com', 'tester1', 'ACTIVE', NOW(), NULL),
       (2, 'KAKAO', 'kakao_67890', 'test2@test.com', 'tester2', 'ACTIVE', NOW(), NULL),
       (3, 'KAKAO', 'kakao_11111', 'test3@test.com', 'tester3', 'ACTIVE', NOW(), NULL);

-- Product 데이터 (기존 데이터가 없다면)
INSERT INTO product (id, name, brand, daily_rental_price, launched_at, registered_at, thumbnail_url)
VALUES (1, 'SONY WH-1000XM6', 'SONY', 10000.00, '2025-01-01 00:00:00', NOW(), 'thumbnail_url'),
       (2, 'BOSE QuietComfort Ultra', 'BOSE', 12000.00, '2025-01-01 00:00:00', NOW(), 'thumbnail_url'),
       (3, 'Apple AirPods Max', 'APPLE', 15000.00, '2025-01-01 00:00:00', NOW(), 'thumbnail_url');

-- Product 색상 데이터
INSERT INTO product_colors (product_id, colors)
VALUES (1, 'BLACK'),
       (1, 'WHITE'),
       (2, 'BLACK'),
       (2, 'WHITE_SMOKE'),
       (3, 'STARLIGHT'),
       (3, 'MIDNIGHT');

INSERT INTO cart (id, member_id, product_id, price, color)
VALUES (1, 1, 1, 10000.00, 'BLACK'),       -- kakao_12345 사용자의 SONY 헤드폰 BLACK
       (2, 1, 2, 12000.00, 'WHITE_SMOKE'), -- kakao_12345 사용자의 BOSE 헤드폰 WHITE_SMOKE
       (3, 1, 3, 15000.00, 'STARLIGHT'),   -- kakao_12345 사용자의 Apple 헤드폰 STARLIGHT
       (4, 2, 1, 10000.00, 'WHITE'),       -- kakao_67890 사용자의 SONY 헤드폰 WHITE
       (5, 2, 2, 12000.00, 'BLACK'),       -- kakao_67890 사용자의 BOSE 헤드폰 BLACK
       (6, 3, 3, 15000.00, 'MIDNIGHT');
-- kakao_11111 사용자의 Apple 헤드폰 MIDNIGHT

-- Rental 데이터
INSERT INTO rental (id, member_id, rental_number, total_price, rental_status, created_at, start_at, end_at, review_status)
VALUES (1, 1, 'CH-25090213363012345678', 37000.00, 'ACTIVE', '2025-01-10 10:00:00', '2025-01-15', '2025-01-22', 'AVAILABLE'),
       (2, 2, 'CH-25091113363012345679', 37000.00, 'ACTIVE', '2025-01-11 14:00:00', '2025-01-16', '2025-01-19', 'AVAILABLE'),
       (3, 3, 'CH-25090513363012345680', 37000.00, 'COMPLETED', '2025-01-05 09:00:00', '2025-01-08', '2025-01-11', 'AVAILABLE');

-- RentalItem 데이터
INSERT INTO rental_item (id, product_id, rental_id, price, color)
VALUES (1, 1, 1, 10000.00, 'BLACK'),
       (2, 2, 1, 12000.00, 'BLACK'),
       (3, 3, 1, 15000.00, 'STARLIGHT'),
       (4, 2, 2, 12000.00, 'WHITE_SMOKE'),
       (5, 1, 2, 10000.00, 'WHITE'),
       (6, 3, 2, 15000.00, 'MIDNIGHT'),
       (7, 3, 3, 15000.00, 'STARLIGHT'),
       (8, 1, 3, 10000.00, 'BLACK'),
       (9, 2, 3, 12000.00, 'BLACK');

-- Payment 메인 데이터
INSERT INTO payment (id, member_id, rental_id,
    -- PaymentInfo 필드들
                     status, payment_method,
    -- PaymentAmount 필드들
                     rental_amount, total_amount, shipping_fee, cleaning_fee,
    -- RentalPeriod 필드들
                     rental_started_at, rental_ended_at,
    -- PaymentDetail 필드들
                     created_at, completed_at)
VALUES (1, 1, 1,
        'COMPLETED', 'KAKAO_PAY',
        37000.00, 42000.00, 3000.00, 2000.00,
        '2025-01-15', '2025-01-22',
        '2025-01-10 10:30:00', '2025-01-10 11:00:00'),

       (2, 2, 2,
        'COMPLETED', 'CREDIT_CARD',
        37000.00, 41500.00, 2500.00, 2000.00,
        '2025-01-16', '2025-01-19',
        '2025-01-11 14:15:00', '2025-01-11 15:00:00'),

       (3, 3, 3,
        'COMPLETED', 'TOSS_PAY',
        37000.00, 41000.00, 2000.00, 2000.00,
        '2025-01-08', '2025-01-11',
        '2025-01-05 09:20:00', '2025-01-05 10:00:00'),

       (4, 1, 1,
        'PENDING', 'NAVER_PAY',
        25000.00, 28000.00, 2000.00, 1000.00,
        '2025-01-20', '2025-01-25',
        '2025-01-15 11:00:00', NULL);

-- PaymentItem 데이터 (payment_items 테이블)
INSERT INTO payment_items (payment_id, payment_item_idx, product_name, brand, color, price)
VALUES
-- Payment 1의 아이템들
(1, 0, 'SONY WH-1000XM6', 'SONY', 'BLACK', 10000.00),
(1, 1, 'BOSE QuietComfort Ultra', 'BOSE', 'BLACK', 12000.00),
(1, 2, 'Apple AirPods Max', 'APPLE', 'STARLIGHT', 15000.00),

-- Payment 2의 아이템들
(2, 0, 'BOSE QuietComfort Ultra', 'BOSE', 'WHITE_SMOKE', 12000.00),
(2, 1, 'SONY WH-1000XM6', 'SONY', 'WHITE', 10000.00),
(2, 2, 'Apple AirPods Max', 'APPLE', 'MIDNIGHT', 15000.00),

-- Payment 3의 아이템들
(3, 0, 'Apple AirPods Max', 'APPLE', 'STARLIGHT', 15000.00),
(3, 1, 'SONY WH-1000XM6', 'SONY', 'BLACK', 10000.00),
(3, 2, 'BOSE QuietComfort Ultra', 'BOSE', 'BLACK', 12000.00),

-- Payment 4의 아이템들 (미결제)
(4, 0, 'SONY WH-1000XM6', 'SONY', 'BLACK', 20000.00),
(4, 1, 'Apple AirPods Max', 'APPLE', 'STARLIGHT', 5000.00);