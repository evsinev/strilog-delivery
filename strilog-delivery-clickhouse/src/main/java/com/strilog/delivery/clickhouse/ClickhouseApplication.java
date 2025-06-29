package com.strilog.delivery.clickhouse;

import com.payneteasy.yaml2json.YamlParser;
import com.strilog.delivery.clickhouse.service.clickhouse.impl.ClickhouseClientImpl;
import com.strilog.delivery.clickhouse.service.clickhouse.model.ClickhouseConnection;
import com.strilog.delivery.clickhouse.service.config.TClickhouseConfig;
import com.strilog.delivery.clickhouse.service.config.TClickhouseConfigTable;
import com.strilog.delivery.core.ThreadCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

public class ClickhouseApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ClickhouseApplication.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: ClickhouseApplication <config-file>");
            System.exit(1);
        }

        YamlParser      yamlParser = new YamlParser();
        ThreadCondition condition  = new ThreadCondition(Duration.ofSeconds(10), LOG);

        do {
            TClickhouseConfig config = yamlParser.parseFile(new File(args[0]), TClickhouseConfig.class);
            for (TClickhouseConfigTable table : config.getTables()) {
                LOG.debug("Processing table {}", table.getTable());
                ClickhouseClientImpl clickhouse = createClickhouseClient(table);
                for (String dir : table.getDirs()) {
                    ProcessQueueDir processQueueDir = new ProcessQueueDir(clickhouse, new File(dir));
                    processQueueDir.processAppDir();
                }
            }
        } while (condition.awaitCanRun());
    }

    private static ClickhouseClientImpl createClickhouseClient(TClickhouseConfigTable table) {
        return new ClickhouseClientImpl(
                ClickhouseConnection.builder()
                        .hostname ( table.getHost())
                        .port     ( table.getPort())
                        .username ( table.getUsername())
                        .password ( table.getPassword())
                        .table    ( table.getTable())
                        .build()
        );
    }

}
