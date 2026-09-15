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

## Reservations Service

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/hotels/available | Consulta hoteles disponibles en una ciudad y rango de fechas (cliente de Hotels Service) |

> Pendiente: este servicio todavía no expone endpoints CRUD propios de reservas (`/api/reservations`).

## Reviews Service

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/reviews | Crear reseña |
| GET | /api/reviews | Obtener todas las reseñas |
| GET | /api/reviews/{id} | Obtener reseña por ID |
| PUT | /api/reviews/{id} | Actualizar reseña |
| DELETE | /api/reviews/{id} | Eliminar reseña |
