## Logs delivery System

A reliable system for collecting, delivering, and storing jsonl log files in Clickhouse database.

## Table of Contents
 
- [Overview](#overview)
- [Components](#components)
    - [Delivery Sender](#delivery-sender)
    - [Delivery Receiver](#delivery-receiver)
    - [Clickhouse Delivery](#clickhouse-delivery)
- [Configuration Examples](#configuration-examples)
- [Building the Project](#building-the-project)
- [Deployment](#deployment)
- [Running the Applications](#running-the-applications)
- [Requirements](#requirements)
- [License](#license)

## Overview

Strilog Delivery is a modular application designed to efficiently transfer JSONL (JSON Lines) log files from source systems to a Clickhouse database.

The system consists of three main components:

1. **Delivery Sender** - Collects log files from specified directories and sends them to the receiver
2. **Delivery Receiver** - Accepts log files from senders and stores them temporarily
3. **Clickhouse Delivery** - Processes received log files and loads them into Clickhouse database tables

## Components

### Delivery Sender

The sender component monitors configured directories for log files and sends them to a receiver endpoint. It supports:
- Multiple directory groups with different endpoints
- Basic authentication
- Automatic hostname detection
- Configurable polling interval

### Delivery Receiver

The receiver component accepts log files from senders and stores them in a structured directory hierarchy for further processing.

### Clickhouse Delivery

The Clickhouse component processes the received log files and loads them into specified Clickhouse database tables.

## Configuration Examples

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

## Building the Project

Build all components with the following command:

```shell
./mvnw clean package -P sender-shaded,receiver-shaded,clickhouse-shaded
```

This will create standalone JAR files for each component with all dependencies included.

## Running the Applications

### Sender
``` 
java -jar strilog-delivery-sender.jar /path/to/sender-config.yaml
```

### Receiver
```
export JETTY_PORT="8087" 
export JETTY_CONTEXT="/delivery-receiver" 
export RECEIVER_DIR="./receiver-queue"
java -jar strilog-delivery-receiver.jar
```

### Clickhouse Delivery
``` 
java -jar strilog-delivery-clickhouse.jar /path/to/delivery-clickhouse-config.yaml
```

## Requirements
- Java 21 or higher
- Maven for building (or use the included Maven wrapper)
- Clickhouse database instance for storage

## License

Apache
