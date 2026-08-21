# Arquitectura de StayBook

## Visión general

StayBook está compuesto por varios microservicios independientes que se comunican mediante HTTP.

## Responsabilidades

### Hotels Service

Gestiona el catálogo de hoteles.

### Reservations Service

Gestiona las reservas y consulta información del servicio de hoteles.

### Reviews Service

Gestiona las valoraciones de los hoteles.

## Comunicación

Reservations Service ───► Hotels Service

Reviews Service ───────► Hotels Service

## Base de datos

Cada servicio posee su propia base de datos PostgreSQL.