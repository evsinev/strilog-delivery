package com.strilog.delivery.clickhouse.service.clickhouse.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class ClickhouseConnection {
    String hostname;
    int    port;
    String table;
    String username;
    String password;
}
