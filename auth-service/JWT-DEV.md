JWT secret (local development)

This service requires a `JWT_SECRET` environment variable to sign JWTs. Do NOT commit real secrets into `application.properties`.

For local development you can set it before running the app:

```bash
export JWT_SECRET="dev-only-secret-CHANGE-ME-not-for-real-use-32chars"
mvn -pl auth-service spring-boot:run
```

Or provide it when running the JAR:

```bash
JWT_SECRET="dev-only-secret-CHANGE-ME-not-for-real-use-32chars" java -jar auth-service/target/auth-service.jar
```

When deploying to any non-local environment, set `JWT_SECRET` from your secrets manager or environment configuration and rotate it if it may have been exposed.
