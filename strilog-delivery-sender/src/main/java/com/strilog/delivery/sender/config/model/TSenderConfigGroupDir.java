package com.strilog.delivery.sender.config.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TSenderConfigGroupDir {
    String                dir;
    String                queue;
    String                app;
}
