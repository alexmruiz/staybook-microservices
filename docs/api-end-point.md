# Endpoints de la API

## Hotels Service

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/hotels | Crear hotel |
| GET | /api/hotels | Obtener hoteles (paginado: `page`, `size`, `sort`) |
| GET | /api/hotels/{id} | Obtener hotel por ID |
| PUT | /api/hotels/{id} | Actualizar hotel |
| DELETE | /api/hotels/{id} | Eliminar hotel |
| POST | /api/cities | Crear ciudad |
| GET | /api/cities | Obtener ciudades (paginado: `page`, `size`, `sort`) |
| GET | /api/cities/{id} | Obtener ciudad por ID |
| PUT | /api/cities/{id} | Actualizar ciudad |
| DELETE | /api/cities/{id} | Eliminar ciudad |
| GET | /api/cities/{cityId}/hotels | Obtener los hoteles de una ciudad |
| NOTE | Inicialización SQL | `hotels-service/src/main/resources/import-hotels.sql` se ejecuta si `spring.sql.init.mode=always`. En pruebas esto puede provocar conflictos de datos. |
| POST | /api/amenities | Crear servicio (amenity) |
| GET | /api/amenities | Obtener servicios (paginado: `page`, `size`, `sort`) |
| GET | /api/amenities/{id} | Obtener servicio por ID |
| PUT | /api/amenities/{id} | Actualizar servicio |
| DELETE | /api/amenities/{id} | Eliminar servicio |

## Booking Service

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/bookings | Crear reserva |
| GET | /api/bookings | Obtener reservas filtradas por usuario (query param: `userId`, paginado: `page`, `size`, `sort`) |
| GET | /api/bookings/{bookingId} | Obtener reserva por ID |
| GET | /api/bookings/{bookingId}/details | Obtener detalles de la reserva (incluye `hotel` y `reviews` cuando estén disponibles) |


## Reviews Service

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/reviews | Crear reseña |
| GET | /api/reviews | Obtener todas las reseñas |
| GET | /api/reviews/{id} | Obtener reseña por ID |
| GET | /api/reviews/hotels | Obtener reseñas por hotel (query param: `hotelId`) |
| PUT | /api/reviews/{id} | Actualizar reseña |
| DELETE | /api/reviews/{id} | Eliminar reseña |
