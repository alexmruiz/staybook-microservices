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

- Hotels Service → Gestión de hoteles.
- Reservations Service → Gestión de reservas.
- Reviews Service → Gestión de opiniones.

Cada microservicio dispone de:

- API REST.
- Base de datos PostgreSQL independiente.
- Documentación Swagger.
- Tests propios.

---

## 📁 Estructura del proyecto

```text
staybook-microservices/
├── hotels-service/
├── reservations-service/
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

Servicios disponibles:

| Servicio | URL |
|----------|-----|
| Hotels API | http://localhost:8081 |
| Reservations API | http://localhost:8082 |
| Reviews API | http://localhost:8083 |
| pgAdmin | http://localhost:5050 |

---

## 📚 Swagger

Cada servicio expone su documentación OpenAPI.

| Servicio | Swagger |
|----------|---------|
| Hotels | `/swagger-ui/index.html` |
| Reservations | `/swagger-ui/index.html` |
| Reviews | `/swagger-ui/index.html` |

Ejemplo:

http://localhost:8081/swagger-ui/index.html

---

## 🗺️ Roadmap

- [x] Migración de RestTemplate.
- [ ] DTO + Mapper.
- [ ] Bean Validation.
- [ ] Global Exception Handler.
- [ ] OpenFeign.
- [ ] JWT Authentication.
- [ ] Unit Testing.
- [ ] Integration Testing.
- [ ] Dockerización completa.
- [ ] Actuator.
- [ ] GitHub Actions CI.
- [ ] SonarCloud.

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