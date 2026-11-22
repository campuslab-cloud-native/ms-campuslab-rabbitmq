# Changelog

Todos los cambios relevantes de este repositorio se documentan aquí.
El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y el versionamiento sigue [SemVer](https://semver.org/lang/es/).

## [Sin publicar]

- El Compose forma el clúster de dos nodos automáticamente (`rabbitmq.conf` con `classic_config`).
- Se deja de versionar el binario de Maven en `.mvn/`.

## [1.0.0] - 2026-11-22

Primera versión estable.

- Topología oficial de RabbitMQ (exchanges direct, topic y dead-letter, colas y DLQ).
- Administrador REST de colas, exchanges y bindings en `/api/v1/rabbit`.
- Compose de dos nodos RabbitMQ con healthchecks.
