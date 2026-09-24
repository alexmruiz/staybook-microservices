# Development Notes

## Inicialización de datos

- `hotels-service` incluye un script de datos en `src/main/resources/import-hotels.sql` que se carga automáticamente por Spring Boot cuando `spring.sql.init.mode` está configurado como `always`.
- En entornos de test, esto puede provocar colisiones con datos insertados por pruebas unitarias o de integración. Para los tests que gestionan sus propios datos use:
  - `@DataJpaTest(properties = "spring.sql.init.mode=never")` para desactivar la importación global.
  - Mover scripts de test a `src/test/resources` y controlarlos con `@Sql` cuando se necesiten.

## Errores de path variables inválidos

- Si un cliente envía literalmente `"{id"` en lugar de un número, el servidor lanzará `MethodArgumentTypeMismatchException` y devolverá HTTP 400 con un mensaje legible.
- El proyecto incluye un manejador global en `hotels-service` que captura `MethodArgumentTypeMismatchException` y convierte el error en un `400 Bad Request`.

## Ejecución de tests

Ejecutar los tests por módulo:

```bash
mvn -f hotels-service test
mvn -f booking-service test
mvn -f reviews-service test
```

Si un test falla por violación de unicidad en la base de datos (por ejemplo `cities.name`), revisa si `import-hotels.sql` está siendo ejecutado en el contexto de la prueba y desactívalo o usa datos únicos en la prueba.

## Recomendaciones

- Para evitar sorpresas, mover la inicialización de datos de ejemplo a `src/test/resources` o condicionar su ejecución mediante profiles (`spring.profiles.active`).
- Usar `spring.jpa.hibernate.ddl-auto=update` en entornos de desarrollo si se quiere conservar datos entre reinicios; para producción usar migraciones gestionadas (Flyway/Liquibase).
