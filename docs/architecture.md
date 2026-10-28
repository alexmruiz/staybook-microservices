# Arquitectura de StayBook

## Visión general

StayBook es una aplicación de reservas de hoteles formada por servicios independientes de catálogo, reservas, reseñas, autenticación y notificaciones. Los servicios síncronos se comunican mediante HTTP y exponen sus API públicas a través de un API Gateway. Notifications está preparado para la futura comunicación asíncrona mediante RabbitMQ. El proyecto también incluye un servidor de configuración.

```text
                         ┌────────────────────┐
Cliente ── HTTP ───────► │ Gateway (8090)     │
                         └─────────┬──────────┘
                                   │ enruta las solicitudes
              ┌────────────────────┼─────────────────────┐
              ▼                    ▼                     ▼
       Hotels Service        Booking Service       Reviews Service
          (8080)                 (8082)                 (8081)
              ▲                    │  ╲                  ▲
              └────────────────────┘   └──────────────────┘
                    HTTP a hoteles       HTTP a reviews
              Hotels Service ─────── HTTP ───────► Reviews Service

                         Auth Service (8083)

                 Booking ── eventos ──► RabbitMQ
                                          │
                                          ▼
                                  Notifications Service
                                  PostgreSQL + Mailpit
```

El gateway enruta las rutas de hoteles, reservas, valoraciones y autenticación al servicio correspondiente. Los servicios también pueden comunicarse directamente entre sí; por ejemplo, Booking consulta Hotels y Reviews, y Hotels consulta Reviews para obtener resúmenes de valoraciones.

Notifications escucha la cola `notifications.booking.events` de RabbitMQ y no tiene una API REST pública ni una ruta en el Gateway. Booking publica eventos después de confirmar la transacción de reserva; el servicio consume las confirmaciones y cancelaciones, registra los identificadores procesados en su propia base de datos y envía un correo por SMTP. La configuración local de [infra/docker-compose.yml](../infra/docker-compose.yml) levanta RabbitMQ, la base de datos PostgreSQL independiente de Notifications y Mailpit.

## Servicios y responsabilidades

| Componente | Responsabilidad |
|---|---|
| **Hotels Service** | Gestiona el catálogo de hoteles, ciudades y servicios disponibles. |
| **Booking Service** | Gestiona las reservas y la disponibilidad de habitaciones; consulta Hotels y Reviews cuando necesita sus datos. |
| **Reviews Service** | Gestiona las valoraciones de los hoteles. |
| **Auth Service** | Gestiona el registro, el inicio de sesión y los perfiles de usuario; su API define autenticación mediante JWT. |
| **Notifications Service** | Consume eventos de confirmación y cancelación de reservas desde RabbitMQ, evita reprocesar identificadores registrados y envía correos SMTP; mantiene su propia base de datos. |
| **Gateway Service** | Enruta las solicitudes HTTP entrantes hacia las API de los servicios. |
| **Config Server** | Ofrece configuración externa basada en los archivos de `config-repo`. La importación del servidor de configuración es opcional en los servicios que la declaran. |

Las llamadas de Booking a Hotels y Reviews usan clientes HTTP basados en `RestClient`. Hotels consulta Reviews mediante OpenFeign. Para notificaciones asíncronas, Booking publica en el exchange topic durable `booking.events.exchange` después del commit de la transacción. Notifications enlaza a ese exchange la cola durable `notifications.booking.events` con las claves `booking.confirmed` y `booking.cancelled`, y procesa mensajes JSON. Los reintentos del listener están habilitados (hasta 3 intentos); la deduplicación se basa en `eventId` persistido en `notifications_db`. El correo se envía al destinatario predeterminado configurado, no a una dirección obtenida del usuario.

## Persistencia

Cada microservicio de dominio mantiene su propia base de datos PostgreSQL:

| Servicio | Base de datos |
|---|---|
| Hotels Service | `hotels_db` |
| Booking Service | `booking_db` |
| Reviews Service | `reviews_db` |
| Auth Service | `auth_db` |
| Notifications Service | `notifications_db` |

Las entidades de persistencia pertenecen a su servicio y no se comparten entre microservicios. Notifications no accede a las bases de datos de Booking o Auth. El Gateway y el Config Server no tienen una base de datos de dominio configurada.

En desarrollo local, `auth_db`, `hotels_db`, `booking_db` y `reviews_db` pueden ejecutarse en el PostgreSQL existente del entorno del desarrollador. `notifications_db` se proporciona mediante el Compose de infraestructura y se publica en `localhost:5433` para evitar conflictos con PostgreSQL en `localhost:5432`.

## Puertos locales

| Componente | Puerto |
|---|---:|
| Hotels Service | 8080 |
| Reviews Service | 8081 |
| Booking Service | 8082 |
| Auth Service | 8083 |
| Gateway Service | 8090 |
| Config Server | 8888 |
| Notifications Service | 8084 (consumidor interno; sin API REST pública) |

## Infraestructura de mensajería y correo

El Compose de [infraestructura](../infra/docker-compose.yml) define:

| Componente | Puerto | Propósito |
|---|---:|---|
| RabbitMQ | 5672 | Broker AMQP |
| RabbitMQ Management | 15672 | Panel web |
| PostgreSQL Notifications | 5433 | Persistencia de Notifications |
| Mailpit SMTP | 1025 | Servidor SMTP local |
| Mailpit Web | 8025 | Visualización de correos |

El remitente y el destinatario predeterminado se configuran con `notifications.mail.from` y `notifications.mail.default-recipient`. Los hosts y puertos externos pueden ajustarse mediante variables de entorno; no se necesita configurar una ruta del Gateway para que Notifications consuma los mensajes.

Para iniciar esta infraestructura:

```bash
docker compose -f infra/docker-compose.yml up -d
```

Los volúmenes `rabbitmq-data` y `notifications-db-data` mantienen los datos aunque se eliminen los contenedores con `docker compose ... down`. `down -v` elimina también esos volúmenes.
