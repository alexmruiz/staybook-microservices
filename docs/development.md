# Development Notes

## Inicialización de datos

- `hotels-service` incluye un script de datos en `src/main/resources/import-hotels.sql` que se carga automáticamente por Spring Boot cuando `spring.sql.init.mode` está configurado como `always`.
- En entornos de test, esto puede provocar colisiones con datos insertados por pruebas unitarias o de integración. Para los tests que gestionan sus propios datos use:
  - `@DataJpaTest(properties = "spring.sql.init.mode=never")` para desactivar la importación global.
  - Mover scripts de test a `src/test/resources` y controlarlos con `@Sql` cuando se necesiten.

Nota importante: algunos módulos incluyen un `application-test.properties` que actualmente tiene `spring.sql.init.mode=always`. Esto provoca que los scripts de `src/main/resources` (por ejemplo `import-hotels.sql`) se ejecuten durante tests y ocasione conflictos de datos o fallos por duplicidad. Recomendaciones:

- Dejar por defecto `spring.sql.init.mode=never` en el perfil de test y activar la importación sólo en los tests que la requieran (con `@Sql` o un profile `integration`).
- Para pruebas de integración que necesitan los datos del script, use Testcontainers (Postgres) o active explícitamente la importación en un profile `integration` (no en el profile de unidad).
- Evite depender de scripts en `src/main/resources` para pruebas unitarias; mueva datos de ejemplo a `src/test/resources` si son necesarios en muchas pruebas.

## Errores de path variables inválidos

- Si un cliente envía literalmente `"{id}"` en lugar de un número, el servidor lanzará `MethodArgumentTypeMismatchException` y devolverá HTTP 400 con un mensaje legible.
- El proyecto incluye un manejador global en `hotels-service` que captura `MethodArgumentTypeMismatchException` y convierte el error en un `400 Bad Request`.

Nota: el manejador global se encuentra en `hotels-service/src/main/java/.../GlobalExceptionHandler.java` y utiliza `ProblemDetail` para devolver respuestas consistentes. Asegúrate de no duplicar lógica similar en otros módulos para mantener comportamientos uniformes.

## Ejecución de tests

Ejecutar los tests por módulo:

```bash
mvn -f hotels-service test
mvn -f booking-service test
mvn -f reviews-service test
```

Si un test falla por violación de unicidad en la base de datos (por ejemplo `cities.name`), revisa si `import-hotels.sql` está siendo ejecutado en el contexto de la prueba y desactívalo o usa datos únicos en la prueba.

Alternativa (desde la raíz del repo, útil en monorepos):

```bash
# Ejecutar tests de un módulo preciso
mvn -pl booking-service test

# Ejecutar tests de un módulo y compilar los módulos necesarios
mvn -pl booking-service -am test
```

Consejo: en CI, separar los jobs en "unit" (rápidos, sin inicializaciones globales) e "integration" (lentos, pueden usar Testcontainers o importar scripts). Mantener los tests unitarios independientes de `src/main/resources` reduce flakiness.

## Recomendaciones

- Para evitar sorpresas, mover la inicialización de datos de ejemplo a `src/test/resources` o condicionar su ejecución mediante profiles (`spring.profiles.active`).
- Usar `spring.jpa.hibernate.ddl-auto=update` en entornos de desarrollo si se quiere conservar datos entre reinicios; para producción usar migraciones gestionadas (Flyway/Liquibase).

Adicionalmente:

- No use `spring.jpa.hibernate.ddl-auto=update` en CI ni producción; emplee migraciones (Flyway/Liquibase) para cambios de esquema reproducibles.
- Documente en el `README` del módulo cuándo y cómo se ejecutan los scripts de inicialización (profile requerido, ubicación del script, etc.).
