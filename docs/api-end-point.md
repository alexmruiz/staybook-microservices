# Referencia de la API REST

Este documento resume las rutas implementadas por los controladores de StayBook. En el entorno local, el punto de entrada común es el Gateway:

```text
http://localhost:8090
```

El Gateway enruta las rutas de cada servicio y exige un token JWT Bearer en todas ellas, excepto `POST /api/auth/register` y `POST /api/auth/login`. Los puertos directos de los servicios se reservan para ejecución y diagnóstico local; para una petición normal, usa la URL del Gateway.

Para autenticarse, envía el token devuelto por el inicio de sesión en la cabecera:

```http
Authorization: Bearer <token>
```

## Auth Service

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/auth/register` | Registra un usuario. No requiere token. |
| `POST` | `/api/auth/login` | Autentica al usuario y devuelve un token JWT. No requiere token. |
| `GET` | `/api/users/me` | Devuelve el perfil del usuario autenticado. |
| `PUT` | `/api/users/me` | Actualiza el perfil del usuario autenticado. |

Las dos rutas de `/api/users/me` requieren un token válido.

## Hotels Service

Las consultas paginadas aceptan `page` (índice desde cero), `size` y `sort` (por ejemplo, `sort=name,asc`).

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/hotels` | Crea un hotel. |
| `GET` | `/api/hotels` | Devuelve una página de hoteles. |
| `GET` | `/api/hotels/{id}` | Devuelve un hotel por su identificador. |
| `GET` | `/api/hotels/{id}/details` | Devuelve el hotel con sus reseñas y la valoración media. |
| `PUT` | `/api/hotels/{id}` | Actualiza un hotel. |
| `DELETE` | `/api/hotels/{id}` | Elimina un hotel. |
| `POST` | `/api/cities` | Crea una ciudad. |
| `GET` | `/api/cities` | Devuelve una página de ciudades. |
| `GET` | `/api/cities/{id}` | Devuelve una ciudad por su identificador. |
| `PUT` | `/api/cities/{id}` | Actualiza una ciudad. |
| `DELETE` | `/api/cities/{id}` | Elimina una ciudad. |
| `GET` | `/api/cities/{cityId}/hotels` | Devuelve los hoteles de una ciudad. |
| `POST` | `/api/cities/import?country={country}` | Importa ciudades para el país indicado. Requiere el rol `ROLE_ADMIN`. |
| `POST` | `/api/amenities` | Crea un servicio o amenity. |
| `GET` | `/api/amenities` | Devuelve una página de amenities. |
| `GET` | `/api/amenities/{id}` | Devuelve un amenity por su identificador. |
| `PUT` | `/api/amenities/{id}` | Actualiza un amenity. |
| `DELETE` | `/api/amenities/{id}` | Elimina un amenity. |

## Booking Service

Las rutas de reservas requieren autenticación. El servicio obtiene el identificador del usuario desde el claim `userId` del JWT para limitar las consultas a las reservas de ese usuario. La lista acepta los parámetros de paginación `page`, `size` y `sort`; no acepta `userId` como parámetro de consulta.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/bookings` | Crea una reserva. |
| `GET` | `/api/bookings` | Devuelve las reservas del usuario autenticado, con paginación. |
| `GET` | `/api/bookings/{bookingId}` | Devuelve una reserva del usuario autenticado. |
| `GET` | `/api/bookings/{bookingId}/details` | Devuelve los detalles de una reserva, con información de hotel y reseñas cuando está disponible. |
| `POST` | `/api/bookings/{bookingId}/confirm` | Confirma una reserva. |
| `POST` | `/api/bookings/{bookingId}/cancel` | Cancela una reserva. |
| `GET` | `/api/room-availability` | Consulta la disponibilidad por hotel, tipo de habitación y rango de fechas. |

`GET /api/room-availability` requiere los parámetros `hotelId`, `roomTypeId`, `startDate`, `endDate` y `requestedRooms`. Las fechas usan el formato ISO `yyyy-MM-dd`; `roomTypeId` y `requestedRooms` deben ser positivos.

Ejemplo:

```text
http://localhost:8090/api/room-availability?hotelId=1&roomTypeId=2&startDate=2026-11-01&endDate=2026-11-05&requestedRooms=1
```

El DTO actual de creación de reservas también incluye `userId` en el cuerpo de la petición, aunque el servicio asigna el propietario a partir del JWT. El claim del token es la fuente de identidad utilizada por el servicio.

## Reviews Service

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/reviews` | Crea una reseña. |
| `GET` | `/api/reviews` | Devuelve todas las reseñas. |
| `GET` | `/api/reviews/{id}` | Devuelve una reseña por su identificador. |
| `GET` | `/api/reviews/hotel/{hotelId}` | Devuelve las reseñas asociadas a un hotel. |
| `GET` | `/api/reviews/hotel/{hotelId}/summary` | Devuelve el resumen de valoraciones de un hotel; Hotels Service lo consume mediante OpenFeign. |
| `PUT` | `/api/reviews/{id}` | Actualiza una reseña. |
| `DELETE` | `/api/reviews/{id}` | Elimina una reseña. |

## Contratos OpenAPI

Cada servicio mantiene su contrato OpenAPI en `src/main/resources/api/`. Estos contratos incluyen los endpoints expuestos y deben actualizarse junto con los controladores cuando cambien rutas, parámetros, autenticación o respuestas.

Consulta la [documentación de arquitectura](./architecture.md) para ver responsabilidades de servicios, puertos locales y comunicación entre componentes.
