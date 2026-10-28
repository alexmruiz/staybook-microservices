# Desarrollo y validación

Esta guía documenta cómo validar los módulos y qué tener en cuenta al trabajar con inicialización de datos, pruebas y cambios de esquema. Los comandos se ejecutan desde la raíz del repositorio.

## Inicialización de datos

Los datos de ejemplo se encuentran en recursos de aplicación:

| Servicio | Script | Configuración actual |
|---|---|---|
| Hotels Service | `hotels-service/src/main/resources/data.sql` | `spring.sql.init.data-locations=classpath:data.sql` |
| Reviews Service | `reviews-service/src/main/resources/data.sql` | `spring.sql.init.data-locations=classpath:data.sql` |

Los archivos `application-test.properties` de Hotels, Booking, Reviews y Auth configuran una base de datos H2 en memoria y también establecen `spring.sql.init.mode=always`. Algunos tests activan explícitamente el perfil `test`. Por tanto, cuando un contexto de prueba carga esos ajustes, Spring puede ejecutar los scripts SQL disponibles en `src/main/resources`; esto puede interferir con las pruebas que preparan sus propios registros o esperan una base vacía. Notifications configura H2 en `src/test/resources/application.properties`, con `ddl-auto=create-drop`, y desactiva el inicio automático del listener RabbitMQ. Sus pruebas unitarias no necesitan servicios externos; la prueba de integración de configuración inicia RabbitMQ con Testcontainers cuando Docker está disponible y se omite si no lo está.

Como excepción deliberada, `HotelRepositoryTest` desactiva la inicialización SQL con `@DataJpaTest(properties = "spring.sql.init.mode=never")`.

### Criterios recomendados

- Usar `spring.sql.init.mode=never` como valor por defecto en los perfiles de prueba.
- Mantener fixtures específicos de pruebas en `src/test/resources` y cargarlos explícitamente con `@Sql` cuando corresponda.
- Si un servicio necesita datos de demostración al iniciarse localmente, habilitar esa carga en una configuración o perfil de desarrollo explícito, en lugar de depender de ella en todos los entornos.
- Al investigar errores de unicidad o resultados inesperados, comprobar qué perfil está activo y si se ejecutan scripts de inicialización antes de cada prueba.

Los cinco servicios que usan persistencia configuran H2 para sus pruebas. H2 facilita pruebas rápidas, pero no reproduce necesariamente todas las diferencias de PostgreSQL. Para validar comportamiento específico de PostgreSQL puede añadirse una suite de integración con Testcontainers; aunque Hotels declara dependencias de Testcontainers, actualmente no hay pruebas que las utilicen.

## Manejo de errores HTTP

Hotels, Booking, Reviews y Auth tienen su propio `GlobalExceptionHandler`. Los manejadores son locales a cada servicio y devuelven respuestas basadas en `ProblemDetail`, pero los errores concretos y sus códigos HTTP dependen de las excepciones que maneja cada uno.

Hotels incluye un caso explícito para `MethodArgumentTypeMismatchException`: cuando un parámetro de ruta tipado recibe un valor que no se puede convertir, responde con HTTP 400 y un `ProblemDetail`. No se debe asumir que los demás servicios tienen el mismo tratamiento para ese error; al incorporar un caso equivalente, añádase al manejador del servicio correspondiente y cúbrase con una prueba.

Al ampliar el manejo de errores:

- Mantener la lógica dentro del servicio dueño del endpoint, evitando acoplar microservicios independientes.
- Preservar los códigos HTTP adecuados para el dominio y una estructura de respuesta coherente.
- Añadir pruebas para el estado HTTP y el cuerpo devuelto, tanto en casos esperados como en errores de validación.

## Ejecutar las pruebas

Desde la raíz, se puede validar cada módulo por separado:

```bash
mvn -pl hotels-service test
mvn -pl booking-service test
mvn -pl reviews-service test
mvn -pl auth-service test
mvn -pl gateway-service test
mvn -pl configserver-service test
mvn -pl notifications-service test
```

Para ejecutar las pruebas de todo el reactor Maven:

```bash
mvn test
```

Si un módulo requiere compilar módulos del mismo reactor de los que depende, añadir `-am` al comando selectivo, por ejemplo:

```bash
mvn -pl booking-service -am test
```

Ante una violación de unicidad o una prueba que encuentra datos inesperados, comprobar primero la configuración del perfil y la ejecución de `data.sql`. Para evitar dependencia entre pruebas, cada una debería crear los datos que necesita y no depender del orden de ejecución.

## Esquema de base de datos y entornos

La configuración actual no es una configuración de producción:

| Servicio | `spring.jpa.hibernate.ddl-auto` |
|---|---|
| Hotels Service | `create-drop` |
| Booking Service | `update` |
| Reviews Service | `update` |
| Auth Service | `update` |
| Notifications Service | `update` (valor predeterminado; configurable con `SPRING_JPA_HIBERNATE_DDL_AUTO`) |

`create-drop` crea el esquema al iniciar y lo elimina al cerrar el contexto de Hibernate; no debe utilizarse cuando se necesita conservar datos. `update` intenta adaptar el esquema automáticamente, pero no ofrece cambios versionados ni reproducibles entre entornos. Además, no garantiza el orden físico de las columnas: aunque un atributo esté declarado primero en una entidad, Hibernate y la base de datos no aseguran que aparezca primero en la tabla; con `update`, las columnas existentes tampoco se reordenan.

El orden de las columnas es principalmente visual y no cambia las claves ni el resultado de las consultas. Si se necesita imponer un orden físico, hay que gestionar el esquema explícitamente mediante SQL o una estrategia de migraciones; no basta con cambiar el orden de los atributos Java. Para que las consultas devuelvan un orden predecible, se deben especificar las columnas en vez de depender de `SELECT *`.

No hay migraciones Flyway o Liquibase configuradas actualmente. Antes de desplegar con datos persistentes, conviene definir una estrategia de migraciones versionadas y usar perfiles de configuración separados para desarrollo, pruebas y producción. No se debe presentar la configuración actual de inicialización o de generación de esquema como preparada para producción.
