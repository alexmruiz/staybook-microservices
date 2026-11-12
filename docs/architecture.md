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
                                   │       │
                              HTTP/Feign  correo
                                   ▼       ▼
                              Auth Service Mailpit
                                  │
                               auth_db
```

El gateway enruta las rutas de hoteles, reservas, valoraciones y autenticación al servicio correspondiente. Los servicios también pueden comunicarse directamente entre sí; por ejemplo, Booking consulta Hotels y Reviews, y Hotels consulta Reviews para obtener resúmenes de valoraciones.

Notifications no tiene una API pública ni una ruta en el Gateway. Booking publica eventos de confirmación y cancelación en RabbitMQ; Notifications consume las claves `booking.confirmed` y `booking.cancelled` desde la cola durable `notifications.booking.events`. El servicio registra los identificadores procesados en su propia base de datos y obtiene de Auth el correo y el nombre del usuario antes de preparar el correo.

La integración HTTP con Auth tiene actualmente una limitación de seguridad: Notifications envía `X-Internal-Api-Key`, pero Auth protege `/internal/users/{userId}` con JWT y no valida esa cabecera. Por tanto, la consulta interna no queda autorizada con la configuración actual y la integración no funciona de extremo a extremo hasta resolver el mecanismo de autenticación entre servicios. La infraestructura local de RabbitMQ, PostgreSQL de Notifications y Mailpit se define en [infra/docker-compose.yml](../infra/docker-compose.yml).

## Servicios y responsabilidades

| Componente | Responsabilidad |
|---|---|
| **Hotels Service** | Gestiona el catálogo de hoteles, ciudades y servicios disponibles. |
| **Booking Service** | Gestiona las reservas y la disponibilidad de habitaciones; consulta Hotels y Reviews cuando necesita sus datos. |
| **Reviews Service** | Gestiona las valoraciones de los hoteles. |
| **Auth Service** | Gestiona el registro, el inicio de sesión y los perfiles de usuario; su API define autenticación mediante JWT. |
| **Notifications Service** | Consume eventos de reserva confirmada o cancelada, registra eventos ya procesados y prepara notificaciones por correo con datos consultados a Auth. Mantiene su propia base de datos. |
| **Gateway Service** | Enruta las solicitudes HTTP entrantes hacia las API de los servicios. |
| **Config Server** | Ofrece configuración externa basada en los archivos de `config-repo`. La importación del servidor de configuración es opcional en los servicios que la declaran. |

Las llamadas de Booking a Hotels y Reviews usan clientes HTTP basados en `RestClient`. Hotels consulta Reviews mediante OpenFeign. Notifications consulta el resumen del usuario en Auth mediante OpenFeign; RabbitMQ se usa para los eventos asíncronos de reservas y no sustituye las consultas HTTP que necesitan una respuesta inmediata.

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
| Notifications Service | 8084 (sin API pública) |

## Infraestructura de mensajería y correo

El Compose de [infraestructura](../infra/docker-compose.yml) define:

| Componente | Puerto | Propósito |
|---|---:|---|
| RabbitMQ | 5672 | Broker AMQP |
| RabbitMQ Management | 15672 | Panel web |
| PostgreSQL Notifications | 5433 | Persistencia de Notifications |
| Mailpit SMTP | 1025 | Servidor SMTP local |
| Mailpit Web | 8025 | Visualización de correos |

Para iniciar esta infraestructura:

```bash
docker compose -f infra/docker-compose.yml up -d
```

Los volúmenes `rabbitmq-data` y `notifications-db-data` mantienen los datos aunque se eliminen los contenedores con `docker compose ... down`. `down -v` elimina también esos volúmenes.
