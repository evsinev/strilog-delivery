package com.strilog.delivery.sender.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Base64;
import java.util.List;

import static com.strilog.delivery.core.SafeFiles.deleteFile;
import static com.strilog.delivery.core.SafeFiles.listFiles;
import static java.util.Collections.sort;

public class SendDir {

    private static final Logger LOG = LoggerFactory.getLogger(SendDir.class);

    private final File     dir;
    private final SendFile sendFile;

    public SendDir(File dir, String baseUrl, String aAuthHeader) {
        this.dir = dir;
        sendFile = new SendFile(baseUrl, aAuthHeader);
    }


    public int sendDir() {
        List<File> files = listFiles(dir, File::isFile);
        if (files.size() <= 1) {
            return 0;
        }

        sort(files);

        LOG.debug("Going to send {} files", files.size() - 1);
        int count = 0;
        for (int i = 0; i < files.size() - 1; i++) {
            File file = files.get(i);
            sendFile.sendFile(file);
            deleteFile(file);
            count++;
        }

        return count;
    }


}
