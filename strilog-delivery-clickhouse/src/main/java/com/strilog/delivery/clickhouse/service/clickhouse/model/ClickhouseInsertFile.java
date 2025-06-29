package com.strilog.delivery.clickhouse.service.clickhouse.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.File;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class ClickhouseInsertFile {

    String table;
    File   file;

}
