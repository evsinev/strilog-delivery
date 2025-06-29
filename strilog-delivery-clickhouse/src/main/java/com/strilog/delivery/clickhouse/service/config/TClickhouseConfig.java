package com.strilog.delivery.clickhouse.service.config;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TClickhouseConfig {
    List<TClickhouseConfigTable> tables;
}
