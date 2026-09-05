# ms-campuslab-rabbitmq

Infraestructura RabbitMQ de CampusLab.

## Tecnologías

- RabbitMQ
- RabbitMQ Management
- Docker
- Docker Compose

## Exchanges

```text
cmd.direct
cmd.topic
cmd.dead.dlx
```

## Colas

```text
q.cmd.email
q.cmd.email.dlq

q.cmd.prep
q.cmd.prep.dlq

q.cmd.voucher
q.cmd.voucher.dlq
```

## Bindings

### Email

```text
Direct: email.send
Topic: email.*
```

### Preparación

```text
Direct: prep.ticket
Topic: prep.#
```

### Voucher

```text
Direct: voucher.gen
Topic: voucher.*
```

## Puertos

```text
5672   AMQP
15672  RabbitMQ Management
```

## Consideraciones

- ACK/NACK explícitos.
- Dead Letter Queues.
- Idempotencia.
- Correlation ID.
- Trace ID.

## Ejecución

```bash
docker compose up -d
```

## Detener

```bash
docker compose down
```
