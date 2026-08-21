# Endpoints de la API

## Hotels Service

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /hotels | Obtener hoteles |
| GET | /hotels/{id} | Obtener hotel |
| POST | /hotels | Crear hotel |
| PUT | /hotels/{id} | Actualizar hotel |
| DELETE | /hotels/{id} | Eliminar hotel |

## Reservations Service

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /reservations | Obtener reservas |
| POST | /reservations | Crear reserva |
| DELETE | /reservations/{id} | Cancelar reserva |

## Reviews Service

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /reviews/hotel/{hotelId} | Obtener opiniones |
| POST | /reviews | Crear opinión |