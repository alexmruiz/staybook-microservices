# 🏨 StayBook Microservices

Sistema de reservas de hoteles desarrollado con **Java 21**, **Spring Boot 3** y arquitectura de **microservicios**.

El proyecto simula una plataforma de reservas donde diferentes servicios colaboran entre sí para gestionar hoteles, reservas y valoraciones.

---

## 🚀 Tecnologías

| Backend | Infraestructura |
|---------|------------------|
| Java 21 | Docker |
| Spring Boot 3 | Docker Compose |
| Spring Data JPA | PostgreSQL |
| Spring Security (JWT próximamente) | pgAdmin |
| OpenFeign / WebClient | GitHub Actions (próximamente) |
| Swagger OpenAPI | SonarCloud |

---

## 🧱 Arquitectura

docs/images/architecture.png

### Microservicios

- **Auth Service** → Autenticación, registro y gestión de perfiles de usuario con JWT.
- **Hotels Service** → Gestión de hoteles.
- **Booking Service** → Gestión de reservas.
- **Reviews Service** → Gestión de opiniones.

Cada microservicio dispone de:

- API REST.
- Base de datos PostgreSQL independiente.
- Documentación Swagger.
- Tests propios.

---

## 📁 Estructura del proyecto

```text
staybook-microservices/
├── auth-service/
├── hotels-service/
├── booking-service/
├── reviews-service/
├── docker-compose.yml
├── docs/
└── README.md
```

---

## ▶️ Cómo ejecutar el proyecto

### Requisitos

- Java 21
- Maven 3.9+
- Docker Desktop

### Levantar el entorno

```bash
docker compose up --build
```

Servicios disponibles (por defecto en local según `application.properties` de cada módulo):

| Servicio | URL |
|----------|-----|
| Auth API | http://localhost:8083 |
| Hotels API | http://localhost:8080 |
| Booking API | http://localhost:8082 |
| Reviews API | http://localhost:8081 |
| pgAdmin | http://localhost:5050 |

---

## 📚 Swagger

Cada servicio expone su documentación OpenAPI.

| Servicio | Swagger |
|----------|---------|
| Hotels | `/swagger-ui/index.html` or `/swagger-ui.html` |
| Booking | `/swagger-ui` or `/swagger-ui.html` |
| Reviews | `/swagger-ui.html` |
| Auth | `/swagger-ui.html` |

Ejemplo:

http://localhost:8081/swagger-ui/index.html

---

## 🗺️ Roadmap

- [x] Migración de RestTemplate.
- [x] DTO + Mapper.
- [x] Bean Validation.
- [x] Global Exception Handler.
- [x] JWT Authentication (Auth Service).
- [ ] OpenFeign.
- [x] Unit Testing.
- [ ] Integration Testing.
- [ ] Dockerización completa.
- [ ] Actuator.
- [ ] GitHub Actions CI.
- [x] SonarCloud.

---

## 📖 Documentación

La documentación técnica se encuentra en la carpeta `docs`.

- Arquitectura.
- Endpoints.
- Flujo de autenticación.
- Diagramas.

---

## 👨‍💻 Autor

Alejandro Moya Ruiz

Backend Developer · Java · Spring Boot · Microservices