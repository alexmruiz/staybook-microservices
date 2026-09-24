-- Incluimos user_id en los inserts porque la columna es NOT NULL
INSERT INTO reviews (hotel_id, user_id, qualification, created_at) VALUES
    (1, 1, 4.5, CURRENT_TIMESTAMP),
    (1, 2, 4.8, CURRENT_TIMESTAMP),
    (2, 3, 3.9, CURRENT_TIMESTAMP),
    (2, 4, 4.2, CURRENT_TIMESTAMP),
    (3, 5, 5.0, CURRENT_TIMESTAMP),
    (3, 6, 4.1, CURRENT_TIMESTAMP),
    (4, 7, 3.5, CURRENT_TIMESTAMP),
    (5, 8, 4.7, CURRENT_TIMESTAMP);