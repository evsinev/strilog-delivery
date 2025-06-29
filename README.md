## Logs delivery
                
Send jsonl files to clickhouse

## Example Configs

### Delivery Sender

```yaml
groups:
  - groupName : group-1
    baseUrl   : https://receiver.internal/delivery-receiver

    basicUsername: username-1
    basicPassword: password-1

    dirs:
      - dir   : /var/app-1/queue-1
        app   : app-1
        queue : queue-1

      - dir   : /var/app-1/queue-2
        app   : app-1
        queue : queue-2

      - dir   : /var/app-2/queue-1
        app   : app-2
        queue : queue-1
```

### Clickhouse delivery

```yaml
tables:
  - host     : localhost
    port     : 8123
    username : username-2
    password : password-2
    table    : logs.table_1
    dirs:
      - /var/delivery-receiver-1/database/host-1/app-1/queue-1
      - /var/delivery-receiver-1/database/host-1/app-1/queue-2

      - /var/delivery-receiver-1/database/host-1/app-2/queue-1
```

## How to build

```shell
./mvnw clean package -P sender-shaded,receiver-shaded,clickhouse-shaded
```
