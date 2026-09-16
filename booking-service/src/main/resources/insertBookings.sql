ALTER TABLE rooms_availability
ADD CONSTRAINT uk_rooms_availability_hotel_room_date
UNIQUE (hotel_id, room_type_id, date);