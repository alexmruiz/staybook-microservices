# Instrucciones para StayBook

## Contexto del proyecto

- StayBook es una aplicación de microservicios para reservas de hoteles.
- Los servicios son `hotels-service`, `reservations-service` y `reviews-service`.
- Cada servicio tiene su propia aplicación Spring Boot y su propia base de datos. La tecnología y la configuración de cada base de datos deben comprobarse en el servicio correspondiente.
- La comunicación entre servicios se realiza mediante HTTP.
- La versión objetivo es Java 21 y el proyecto usa Spring Boot 3.5.5.

## Estructura y responsabilidades

- Mantener el código de cada microservicio dentro de su propia carpeta.
- `hotels-service` gestiona el catálogo de hoteles.
- `reservations-service` gestiona las reservas y consulta hoteles.
- `reviews-service` gestiona las valoraciones y consulta hoteles cuando sea necesario.
- No compartir entidades JPA entre microservicios. Compartir únicamente contratos claramente definidos cuando sea necesario.
- El usuario es un desarrollador junior que está aprendiendo. Proporciona explicaciones detalladas, claras y didácticas sobre los cambios realizados y el motivo de cada decisión.
- Explica los conceptos técnicos paso a paso y utiliza ejemplos sencillos cuando ayuden a comprender la solución.

## Convenciones de código

- Usar nombres de clases y métodos en inglés, siguiendo las convenciones de Java.
- Usar `jakarta.persistence` para las entidades JPA.
- Preferir tipos de `java.time`, como `LocalDateTime` o `Instant`, frente a `java.util.Date`.
- Mantener las entidades JPA separadas de los DTOs.
- Usar `RequestDto` para datos recibidos por la API y `ResponseDto` para datos devueltos por la API.
- No incluir en los DTOs de petición campos generados por el servidor, como `id` o `createdAt`.
- No exponer directamente entidades JPA desde los controladores.
- Usar constructores, getters y setters coherentes con el estilo existente del servicio.
- Mantener los cambios pequeños y evitar refactorizaciones no relacionadas.
- Aplicar buenas prácticas de ingeniería y principios SOLID cuando aporten valor, sin introducir complejidad innecesaria.
- No modificar archivos generados o artefactos de compilación, como `target/`, salvo que se solicite expresamente.
- No cambiar nombres públicos de clases, métodos, propiedades o endpoints sin actualizar sus usos y la documentación correspondiente.

## API y persistencia

- Los controladores deben ocuparse del protocolo HTTP y delegar la lógica al servicio.
- La lógica de negocio debe estar en la capa de servicio.
- Los repositorios deben encargarse del acceso a datos.
- Añadir validación de entrada con Bean Validation cuando se incorporen endpoints o DTOs nuevos.
- Usar `@Valid` en los parámetros de petición y anotaciones como `@NotNull`, `@NotBlank`, `@Positive` o `@DecimalMin` según las reglas del dominio.
- Los campos obligatorios deben reflejarse tanto en la validación del DTO como en las restricciones JPA cuando corresponda.
- Para fechas de creación, usar un valor generado por la aplicación o por JPA y evitar que el cliente pueda modificarlo.
- Crear mappers para convertir entidades a DTOs y evitar exponer entidades JPA desde la API.
- Centralizar el tratamiento de errores HTTP mediante `@RestControllerAdvice` cuando el servicio tenga varios endpoints o excepciones comunes.
- Mantener los nombres y formatos de las propiedades JSON compatibles con los contratos documentados.
- Usar migraciones de base de datos, como Flyway, únicamente si están configuradas en el servicio; no modificar el esquema manualmente sin actualizar la migración correspondiente.

## Pruebas y validación

- Añadir o actualizar pruebas para los cambios de comportamiento.
- Añadir pruebas unitarias para la lógica de negocio y pruebas de controlador o integración cuando se modifique el contrato HTTP o la persistencia.
- Ejecutar las pruebas del módulo modificado desde su propia carpeta antes de finalizar:

  ```bash
  ./mvnw test
  ```

- Para validar todos los microservicios desde la raíz:

  ```bash
  mvn test
  ```

- Revisar que los cambios mantengan la compatibilidad con los endpoints documentados en `docs/`.
- Revisar los resultados de compilación, pruebas y análisis estático cuando estén configurados en el módulo.
- Antes de finalizar, revisar `git diff` y `git status` para detectar cambios accidentales o archivos generados.

## Ejecución local

- Requisitos: Java 21, Maven 3.9+ y Docker Desktop.
- Para levantar un entorno definido por un servicio, ejecutar el comando desde la carpeta que contiene su `docker-compose.yml`:

  ```bash
  cd hotels-service
  docker compose up --build
  ```

- No asumir que existe un `docker-compose.yml` en la raíz. Comprobar primero la configuración y los servicios disponibles.

- No incluir credenciales, tokens ni secretos en el código, los archivos de configuración versionados o los mensajes de commit.
- Antes de modificar la configuración de un servicio, revisar su `application.properties` y su `docker-compose.yml`.
- Preferir variables de entorno para credenciales, URLs, puertos y demás configuración específica del entorno.
- No registrar contraseñas, tokens ni datos sensibles en logs.

## Documentación

- Actualizar `README.md` o la documentación de `docs/` cuando cambien endpoints, arquitectura, puertos o instrucciones de ejecución.
- Mantener los nombres y rutas de los endpoints consistentes con la documentación existente.
- Documentar decisiones que afecten a la arquitectura, comunicación entre servicios, persistencia o ejecución local.
