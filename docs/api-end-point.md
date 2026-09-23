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
| POST | /api/amenities | Crear servicio (amenity) |
| GET | /api/amenities | Obtener servicios (paginado: `page`, `size`, `sort`) |
| GET | /api/amenities/{id} | Obtener servicio por ID |
| PUT | /api/amenities/{id} | Actualizar servicio |
| DELETE | /api/amenities/{id} | Eliminar servicio |

## Booking Service

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/bookings | Crear reserva |
| GET | /api/bookings/{id} | Obtener reserva por ID |
| GET | /api/bookings/all-bookings/{userId} | Obtener todas las reservas de un usuario (paginado: `page`, `size`, `sort`) |


## Reviews Service

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/reviews | Crear reseña |
| GET | /api/reviews | Obtener todas las reseñas |
| GET | /api/reviews/{id} | Obtener reseña por ID |
| GET | /api/reviews/hotels | Obtener reseñas por hotel (query param: `hotelId`) |
| PUT | /api/reviews/{id} | Actualizar reseña |
| DELETE | /api/reviews/{id} | Eliminar reseña |
