-- Script para insertar reseñas iniciales en la tabla 'reviews'
INSERT INTO reviews (hotel_id, qualification, created_at) VALUES
    (1, 4.5, CURRENT_TIMESTAMP),
    (1, 4.8, CURRENT_TIMESTAMP),
    (2, 3.9, CURRENT_TIMESTAMP),
    (2, 4.2, CURRENT_TIMESTAMP),
    (3, 5.0, CURRENT_TIMESTAMP),
    (3, 4.1, CURRENT_TIMESTAMP),
    (4, 3.5, CURRENT_TIMESTAMP),
    (5, 4.7, CURRENT_TIMESTAMP);