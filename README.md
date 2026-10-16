# StayBook

**Plataforma backend de reservas hoteleras construida con Java y Spring Boot.**

StayBook modela un flujo de reservas mediante microservicios independientes: catálogo de hoteles, disponibilidad y reservas, valoraciones, gestión de usuarios y la base preparada para notificaciones asíncronas. El proyecto pone el foco en límites de servicio claros, comunicación HTTP, seguridad basada en JWT y contratos REST documentados con OpenAPI.

> Proyecto de portfolio en evolución. Esta documentación distingue las capacidades implementadas de los aspectos que aún requieren trabajo antes de un despliegue productivo.

## Funcionalidades

| Servicio | Responsabilidad |
|---|---|
| **Hotels Service** | Gestiona hoteles, ciudades y servicios; consulta reseñas para ofrecer detalles y valoraciones agregadas. |
| **Booking Service** | Consulta disponibilidad y gestiona la creación, consulta, confirmación y cancelación de reservas; usa el JWT para asignar el usuario y filtrar las consultas. |
| **Reviews Service** | Gestiona reseñas y proporciona consultas por hotel y resúmenes de valoraciones. |
| **Auth Service** | Registra usuarios, autentica credenciales, emite JWT y permite consultar o actualizar el perfil propio. |
| **Notifications Service** | Servicio preparado para procesar notificaciones de reserva; actualmente contiene el esqueleto del servicio y su conexión a PostgreSQL. |
| **Gateway Service** | Enruta las solicitudes a los servicios y comprueba los JWT de las rutas protegidas. |
| **Config Server** | Proporciona configuración externa a los servicios que usan Spring Cloud Config. |

## Arquitectura

```mermaid
flowchart LR
    Client[Cliente REST] -->|HTTP; JWT salvo registro e inicio de sesión| Gateway[Gateway :8090]
    Gateway --> Auth[Auth :8083]
    Gateway --> Hotels[Hotels :8080]
    Gateway --> Booking[Booking :8082]
    Gateway --> Reviews[Reviews :8081]

    Booking -->|HTTP / RestClient| Hotels
    Booking -->|HTTP / RestClient| Reviews
    Hotels -->|HTTP / OpenFeign| Reviews
    Booking -. eventos de reserva .-> Rabbit[RabbitMQ]
    Rabbit -. futuro consumo .-> Notifications[Notifications :8084]
    Notifications --> NotificationsDB[(notifications_db)]
    Notifications -. correo local .-> Mailpit[Mailpit :8025]

    Auth --> AuthDB[(auth_db)]
    Hotels --> HotelsDB[(hotels_db)]
    Booking --> BookingDB[(booking_db)]
    Reviews --> ReviewsDB[(reviews_db)]

    Config[Config Server :8888] -. configuración .-> Auth
    Config -. configuración .-> Hotels
    Config -. configuración .-> Booking
    Config -. configuración .-> Reviews
    Config -. configuración .-> Gateway
```

Los servicios de dominio mantienen bases de datos PostgreSQL independientes. Las entidades JPA pertenecen a cada servicio; la comunicación entre ellos se realiza mediante HTTP o mensajería, no mediante acceso compartido a sus bases de datos. La integración de eventos entre Booking y Notifications está preparada en la infraestructura local, pero la publicación y el consumo funcional de eventos aún forman parte del trabajo pendiente. Booking obtiene del JWT el usuario asociado a las reservas y usa esa identidad para filtrar las consultas.

El Gateway es el punto de entrada previsto para las API en local. Solo el registro y el inicio de sesión están exentos de JWT en el filtro del Gateway; las demás rutas requieren un token Bearer válido. El endpoint de importación de ciudades requiere además el rol `ROLE_ADMIN`.

## Tecnologías

- **Java 21**, **Spring Boot 3.5.5** y **Spring Cloud 2025.0.0**
- **Maven** como herramienta de compilación y gestión del reactor multimódulo
- **Spring Web**, **Spring Data JPA**, **Bean Validation** y **PostgreSQL**
- **Spring Security** y **JWT** para autenticación y autorización
- **Spring Cloud Gateway** para el enrutamiento de API
- **Spring Cloud OpenFeign** y **Spring `RestClient`** para comunicación HTTP entre servicios
- **RabbitMQ** como broker preparado para eventos de reserva
- **Mailpit** para capturar correos durante el desarrollo local
- **Resilience4j** para tolerancia a fallos en llamadas remotas
- **Springdoc OpenAPI** para documentar los contratos REST
- **JUnit, Spring Boot Test y H2** para pruebas
- **GitHub Actions y SonarCloud** para análisis automatizado del proyecto

## Servicios y puertos locales

| Componente | Puerto | Base de datos |
|---|---:|---|
| Gateway Service | `8090` | — |
| Auth Service | `8083` | `auth_db` |
| Hotels Service | `8080` | `hotels_db` |
| Reviews Service | `8081` | `reviews_db` |
| Booking Service | `8082` | `booking_db` |
| Notifications Service | Sin API pública asignada actualmente | `notifications_db` |
| Config Server | `8888` | — |

El detalle de rutas, métodos, autenticación y parámetros está en la [referencia de la API](./docs/api-end-point.md). La arquitectura y las responsabilidades de cada componente se describen en [docs/architecture.md](./docs/architecture.md).

## Compilar y ejecutar pruebas

### Requisitos

- JDK 21
- Maven 3.9 o posterior
- PostgreSQL para ejecutar localmente los servicios de dominio
- Docker Engine o Docker Desktop para la infraestructura local de Notifications

Desde la raíz del repositorio, Maven puede compilar y probar todos los módulos:

```bash
mvn -B clean verify
```

Para trabajar en un módulo concreto:

```bash
mvn -pl booking-service test
```

Añade `-am` si también necesitas que Maven compile los módulos del reactor requeridos por el módulo seleccionado:

```bash
mvn -pl booking-service -am test
```

## Ejecución local

1. Inicia PostgreSQL y crea las bases `auth_db`, `hotels_db`, `booking_db` y `reviews_db`.
2. Inicia la infraestructura adicional:

   ```bash
   docker compose -f infra/docker-compose.yml up -d
   ```

   Este Compose levanta RabbitMQ, PostgreSQL para `notifications_db` y Mailpit. No levanta todos los servicios de StayBook ni sustituye las bases de datos existentes de los servicios de dominio.

3. Inicia Config Server desde la raíz:

   ```bash
   mvn -pl configserver-service spring-boot:run
   ```

4. En terminales separadas, inicia los servicios:

   ```bash
   mvn -pl auth-service spring-boot:run
   mvn -pl hotels-service spring-boot:run
   mvn -pl reviews-service spring-boot:run
   mvn -pl booking-service spring-boot:run
   mvn -pl gateway-service spring-boot:run
   ```

Hotels y Reviews importan configuración del Config Server de forma opcional; para el funcionamiento normal, inicia Config Server antes que esos servicios. La configuración específica de cada uno está en `configserver-service/src/main/resources/config-repo/`. Para conectar cada proceso a PostgreSQL, configura `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`; esas variables de entorno pueden sobrescribir la configuración remota. Notifications utiliza por defecto `localhost:5433/notifications_db`, que corresponde al puerto publicado por el Compose. Usa la base de datos correspondiente a cada servicio y no guardes credenciales reales en el repositorio. Auth, Hotels, Booking y Gateway deben utilizar el mismo `JWT_SECRET` para emitir y validar tokens; consulta la [guía de configuración JWT](./docs/JWT-DEV.md).

> **Persistencia local:** la configuración actual usa `ddl-auto=create-drop` en Hotels y `ddl-auto=update` en Booking, Reviews y Auth. No conectes esta configuración a datos que deban conservarse ni la consideres preparada para producción. Consulta [desarrollo y validación](./docs/development.md).

## Documentación interactiva

Con cada servicio iniciado, Swagger UI está disponible en:

| Servicio | URL local |
|---|---|
| Hotels | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| Booking | [http://localhost:8082/swagger-ui](http://localhost:8082/swagger-ui) |
| Reviews | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| Auth | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) |

Los contratos OpenAPI fuente están organizados dentro de `src/main/resources/api/` en cada servicio.

## Infraestructura local de notificaciones

El archivo [infra/docker-compose.yml](./infra/docker-compose.yml) proporciona los componentes necesarios para el desarrollo de Notifications:

| Componente | Puerto local | Uso |
|---|---:|---|
| RabbitMQ | `5672` | Conexión AMQP de los servicios |
| RabbitMQ Management | `15672` | Panel de administración |
| PostgreSQL Notifications | `5433` | Base `notifications_db` |
| Mailpit SMTP | `1025` | Captura de correo SMTP |
| Mailpit Web | `8025` | Bandeja local de correos |

Para detener los contenedores sin borrar sus volúmenes:

```bash
docker compose -f infra/docker-compose.yml stop
```

Para eliminarlos conservando los volúmenes:

```bash
docker compose -f infra/docker-compose.yml down
```

No uses `down -v` salvo que quieras borrar también los datos persistidos de RabbitMQ y PostgreSQL.

## Automatización y estado del despliegue

El workflow de [SonarCloud](./.github/workflows/sonar.yml) ejecuta `mvn verify` y el análisis estático al actualizar `main` o abrir/actualizar un pull request. Para que el análisis remoto funcione, el repositorio debe tener configurado `SONAR_TOKEN` como secreto de GitHub Actions.

La contenerización es parcial: existen Dockerfiles para Hotels y Reviews, y `infra/docker-compose.yml` levanta la infraestructura de Notifications, pero no la plataforma completa ni sus servicios. Las bases de datos de Hotels, Booking, Reviews y Auth continúan siendo responsabilidad de la configuración local existente.

Antes de un despliegue productivo todavía habría que definir, entre otros aspectos, migraciones versionadas de base de datos, gestión segura de secretos y una orquestación completa del entorno. Más notas y decisiones están en la carpeta [docs](./docs/).

## Autor

**Alejandro Moya Ruiz**

Desarrollo backend · Java · Spring Boot · Microservicios
