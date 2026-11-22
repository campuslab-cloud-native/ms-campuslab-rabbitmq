# ms-campuslab-rabbitmq

RabbitMQ infrastructure and Rabbit Admin API for CampusLab.

## Local Compose topology

[`compose.yml`](./compose.yml) defines three services:

| Service | Host ports | Purpose |
|---|---|---|
| `rabbitmq-1` | `5672`, `15672` | Broker used by Rabbit Admin in this Compose |
| `rabbitmq-2` | `5673`, `15673` | Second broker; joins the cluster formed with `rabbitmq-1` |
| `rabbit-admin` | `8087` | Spring Boot API for dynamic queues, exchanges and bindings |

Both brokers share the same Erlang cookie and mount [`rabbitmq.conf`](./rabbitmq.conf),
which enables `classic_config` peer discovery: on first start with an empty data
volume, `rabbitmq-2` joins `rabbit@rabbitmq-1` and they form a two-node cluster.
`rabbitmq-2` waits for `rabbitmq-1` to be healthy before starting. `rabbit-admin`
connects only to `rabbitmq-1`.

Check the cluster with:

```bash
docker exec campuslab-rabbitmq-1 rabbitmqctl cluster_status
```

Peer discovery only applies to a volume without previous data. If the volumes
already hold two standalone nodes from an earlier run, recreate them
(`docker compose down -v`, which deletes the queues and messages stored in this
local environment) or join manually with `rabbitmqctl join_cluster`.
The cluster replicates metadata; queues are replicated only if they are declared
as quorum queues or with a mirroring policy, so do not assume failover for the
classic queues declared by this service.

In the deployed AWS environment, the team has confirmed a RabbitMQ cluster.
That cluster is external to this local Compose. Configure clients with the
deployment-provided DNS endpoint; this repository does not contain its
addresses or establish a failover policy.

## Static topology and message flow

`RabbitTopologyConfig` declares durable exchanges:

| Exchange | Type | Routing key | Queue |
|---|---|---|---|
| `cmd.direct` | `direct` | `email.send` | `q.cmd.email` |
| `cmd.direct` | `direct` | `prep.ticket` | `q.cmd.prep` |
| `cmd.direct` | `direct` | `voucher.gen` | `q.cmd.voucher` |
| `cmd.topic` | `topic` | `email.*` | `q.cmd.email` |
| `cmd.topic` | `topic` | `prep.#` | `q.cmd.prep` |
| `cmd.topic` | `topic` | `voucher.*` | `q.cmd.voucher` |
| `cmd.dead.dlx` | `direct` | `dead.email` | `q.cmd.email.dlq` |
| `cmd.dead.dlx` | `direct` | `dead.prep` | `q.cmd.prep.dlq` |
| `cmd.dead.dlx` | `direct` | `dead.voucher` | `q.cmd.voucher.dlq` |

Each main queue has `x-dead-letter-exchange=cmd.dead.dlx` and its matching
dead-letter routing key. Bookings currently publishes to `cmd.direct` using
`email.send`, `prep.ticket` and `voucher.gen`. Notify consumes the three main
queues. No application consumer for the DLQ queues is defined in these
repositories. The message envelope and operations are documented in
[`asyncapi.yaml`](./asyncapi.yaml).

## Rabbit Admin API and security

The API is under `/api/v1/rabbit`; request bodies, responses and methods are
specified in [`openapi.yaml`](./openapi.yaml). It creates/deletes queues,
exchanges and bindings. It has no application-level authentication. Because
these operations can change broker topology, restrict port `8087` to
authorized operators using private networking and Security Group rules; do
not expose it to `0.0.0.0/0`. Restrict Management UI port `15672` to operators
as well. Port `5672` should be reachable only by the application services.

## Configuration and ejecución local

`RABBITMQ_HOST` is `rabbitmq-1` for containers on the Compose network and
`localhost` for a process running on the host. The Rabbit Admin Compose service
is configured to use `rabbitmq-1`.

Copy `.env.example` to `.env` for local development. Its `guest` values are
development defaults only; provide deployment-managed credentials in AWS and
do not commit them.

```bash
docker compose config
docker compose up --build -d
docker compose ps
```

RabbitMQ and Rabbit Admin use `restart: unless-stopped`. The Management UI is
available on `http://localhost:15672` and Rabbit Admin health on
`http://localhost:8087/actuator/health`.

Tests and Compose validation:

```bash
./mvnw clean test
docker compose config
```
