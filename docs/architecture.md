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

Notifications no tiene actualmente una API pública ni una ruta en el Gateway. Su configuración local se encuentra en [infra/docker-compose.yml](../infra/docker-compose.yml), que levanta RabbitMQ, la base de datos PostgreSQL independiente de Notifications y Mailpit. La publicación y el consumo funcional de eventos quedan fuera del esqueleto actual del servicio.

## Servicios y responsabilidades

| Componente | Responsabilidad |
|---|---|
| **Hotels Service** | Gestiona el catálogo de hoteles, ciudades y servicios disponibles. |
| **Booking Service** | Gestiona las reservas y la disponibilidad de habitaciones; consulta Hotels y Reviews cuando necesita sus datos. |
| **Reviews Service** | Gestiona las valoraciones de los hoteles. |
| **Auth Service** | Gestiona el registro, el inicio de sesión y los perfiles de usuario; su API define autenticación mediante JWT. |
| **Notifications Service** | Servicio independiente para futuras notificaciones derivadas de eventos de reservas; mantiene su propia base de datos. |
| **Gateway Service** | Enruta las solicitudes HTTP entrantes hacia las API de los servicios. |
| **Config Server** | Ofrece configuración externa basada en los archivos de `config-repo`. La importación del servidor de configuración es opcional en los servicios que la declaran. |

Las llamadas de Booking a Hotels y Reviews usan clientes HTTP basados en `RestClient`. Hotels consulta Reviews mediante OpenFeign. RabbitMQ se reserva para eventos asíncronos entre servicios; no sustituye las consultas HTTP que necesitan una respuesta inmediata.

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
| Notifications Service | Sin API pública asignada actualmente |

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
