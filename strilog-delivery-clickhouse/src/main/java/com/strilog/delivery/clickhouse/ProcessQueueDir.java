package com.strilog.delivery.clickhouse;

import com.strilog.delivery.clickhouse.service.clickhouse.IClickhouseClient;
import com.strilog.delivery.core.SafeFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static com.strilog.delivery.core.SafeFiles.listFilesSorted;

public class ProcessQueueDir {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessQueueDir.class);

    private final IClickhouseClient clickhouse;
    private final File              queueDir;

    public ProcessQueueDir(IClickhouseClient clickhouse, File baseDir) {
        this.clickhouse = clickhouse;
        this.queueDir   = baseDir;
    }

    public void processAppDir() {
        if (!queueDir.exists()) {
            LOG.warn("Queue dir does not exist: {}", queueDir.getAbsolutePath());
            return;
        }

        List<File> files = listFilesSorted(queueDir, it -> it.isFile() && !it.getName().endsWith(".tmp"));
        if (files.isEmpty()) {
            LOG.debug("Queue dir is empty: {}", queueDir.getAbsolutePath());
            return;
        }

        for (File file : files) {
            LOG.debug("Processing file {}", file.getAbsolutePath());
            insertFile(file);
        }
    }

    private void insertFile(File file) {
        try {
            clickhouse.insertFile(file);
            SafeFiles.deleteFile(file);
        } catch (Exception e) {
            LOG.error("Cannot insert file {}", file.getAbsolutePath(), e);
        }
    }
}
