# ms-campuslab-rabbitmq

## Propósito

Este repositorio entrega la infraestructura de mensajería de CampusLab para la EC2 Messaging. Abarca:

- broker RabbitMQ con Management UI
- microservicio Spring Boot Rabbit Admin para gestionar queues, exchanges y bindings
- topología oficial de CampusLab
- dead-letter exchanges y DLQ para flujo de errores

No incluye lógica de negocio de notify, ni consumidores funcionales; ese comportamiento quedará en el backend de la EC2 Backend.

## Arquitectura

```text
Frontend Angular
    |
    v
AWS API Gateway
    |
    v
ms-campuslab-bff
    |
    +--> ms-campuslab-bookings
    +--> ms-campuslab-catalog
    +--> ms-campuslab-report
    +--> ms-campuslab-audit

ms-campuslab-bookings --> RabbitMQ --> ms-campuslab-notify
```

En la EC2 Messaging se despliegan:

- RabbitMQ broker
- RabbitMQ Management UI
- ms-campuslab-rabbitmq (Rabbit Admin)

## Topología oficial

### Exchanges

```text
cmd.direct        direct
cmd.topic         topic
cmd.dead.dlx      direct
```

### Colas principales

```text
q.cmd.email
q.cmd.prep
q.cmd.voucher
```

### DLQ

```text
q.cmd.email.dlq
q.cmd.prep.dlq
q.cmd.voucher.dlq
```

### Bindings

```text
q.cmd.email -> cmd.direct -> email.send
q.cmd.email -> cmd.topic -> email.*

q.cmd.prep -> cmd.direct -> prep.ticket
q.cmd.prep -> cmd.topic -> prep.#

q.cmd.voucher -> cmd.direct -> voucher.gen
q.cmd.voucher -> cmd.topic -> voucher.*
```

### DLQ routing keys

```text
dead.email
dead.prep
dead.voucher
```

Cada cola principal está configurada con `x-dead-letter-exchange=cmd.dead.dlx` y su routing key de DLQ correspondiente.

## Rabbit Admin REST

La API administrativa expone los recursos bajo el prefijo `/api/v1/rabbit`.

### Endpoints

```http
POST   /api/v1/rabbit/queues
DELETE /api/v1/rabbit/queues/{name}

POST   /api/v1/rabbit/exchanges
DELETE /api/v1/rabbit/exchanges/{name}

POST   /api/v1/rabbit/bindings
DELETE /api/v1/rabbit/bindings
```

## Variables de entorno

Crear un archivo `.env` local con valores reales y no versionar secretos.

```env
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

El servicio Spring Boot usa:

```text
SERVER_PORT=8087
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME
RABBITMQ_PASSWORD
```

## Ejecución local

### 1. Preparar variables de entorno

```bash
cp .env.example .env
```

### 2. Ejecutar RabbitMQ y Rabbit Admin

```bash
docker compose up --build -d
```

### 3. Validar RabbitMQ UI

```text
http://localhost:15672
```

### 4. Validar Rabbit Admin

```text
http://localhost:8087/actuator/health
```

## Docker Compose

La composición levanta dos contenedores:

- `rabbitmq` con RabbitMQ 3 Management
- `rabbit-admin` con el microservicio Spring Boot

Puertos:

```text
5672   AMQP
15672 RabbitMQ Management UI
8087  Rabbit Admin API
```

## Pruebas rápidas

```bash
./mvnw clean test
```

## Validación Docker Compose

```bash
docker compose config
```

## Security Groups recomendados para EC2 Messaging

```text
22    SSH          origen: IP administrador
5672  AMQP         origen: EC2 Backend Security Group
15672 RabbitMQ UI  origen: IP administrador
8087  Rabbit Admin origen: EC2 Backend Security Group / IP administrador
```

No se recomienda abrir 5672 a 0.0.0.0/0.

## Preparación para CI/CD

El repositorio está organizado para facilitar un futuro workflow de CI/CD:

- Maven con pruebas unitarias
- Dockerfile para construir Rabbit Admin
- `docker compose` para validar entorno
- estructura simple y reproducible en EC2 Messaging

## Archivos principales

- `compose.yml`
- `Dockerfile`
- `asyncapi.yaml`
- `application.properties`
- `RabbitTopologyConfig.java`
- `RabbitAdminController.java`
- `RabbitAdminServiceImpl.java`

## Consideraciones de operación

- Se usan colas durables.
- Se configuran DLQ y dead-letter bindings.
- Se mantiene una topología declarativa clara.
- El administrador REST solo gestiona recursos dinámicos; la topología oficial se declara en la configuración estática de Spring. 
