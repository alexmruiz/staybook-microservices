# Arquitectura de StayBook

## Visión general

StayBook es una aplicación de reservas de hoteles formada por cuatro microservicios de dominio independientes. Se comunican mediante HTTP y exponen sus API a través de un API Gateway. El proyecto también incluye un servidor de configuración.

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
```

El gateway enruta las rutas de hoteles, reservas, valoraciones y autenticación al servicio correspondiente. Los servicios también pueden comunicarse directamente entre sí; por ejemplo, Booking consulta Hotels y Reviews, y Hotels consulta Reviews para obtener resúmenes de valoraciones.

## Servicios y responsabilidades

| Componente | Responsabilidad |
|---|---|
| **Hotels Service** | Gestiona el catálogo de hoteles, ciudades y servicios disponibles. |
| **Booking Service** | Gestiona las reservas y la disponibilidad de habitaciones; consulta Hotels y Reviews cuando necesita sus datos. |
| **Reviews Service** | Gestiona las valoraciones de los hoteles. |
| **Auth Service** | Gestiona el registro, el inicio de sesión y los perfiles de usuario; su API define autenticación mediante JWT. |
| **Gateway Service** | Enruta las solicitudes HTTP entrantes hacia las API de los servicios. |
| **Config Server** | Ofrece configuración externa basada en los archivos de `config-repo`. La importación del servidor de configuración es opcional en los servicios que la declaran. |

Las llamadas de Booking a Hotels y Reviews usan clientes HTTP basados en `RestClient`. Hotels consulta Reviews mediante OpenFeign.

## Persistencia

Cada microservicio de dominio mantiene su propia base de datos PostgreSQL:

| Servicio | Base de datos |
|---|---|
| Hotels Service | `hotels_db` |
| Booking Service | `booking_db` |
| Reviews Service | `reviews_db` |
| Auth Service | `auth_db` |

Las entidades de persistencia pertenecen a su servicio y no se comparten entre microservicios. El Gateway y el Config Server no tienen una base de datos de dominio configurada.

## Puertos locales

| Componente | Puerto |
|---|---:|
| Hotels Service | 8080 |
| Reviews Service | 8081 |
| Booking Service | 8082 |
| Auth Service | 8083 |
| Gateway Service | 8090 |
| Config Server | 8888 |
