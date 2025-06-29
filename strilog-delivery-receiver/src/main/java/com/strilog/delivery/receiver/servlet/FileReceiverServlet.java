package com.strilog.delivery.receiver.servlet;

import com.strilog.delivery.receiver.util.PathParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.strilog.delivery.core.SafeFiles.createDirs;

public class FileReceiverServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(FileReceiverServlet.class);

    private record CreatedFile(File tempFile, File logFile) {}

    private final Lock lock = new ReentrantLock();

    private final File baseDir;

    public FileReceiverServlet(File baseDir) {
        this.baseDir = baseDir;
    }


    @Override
    protected void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException {
        PathParameters parameters = new PathParameters(aRequest.getRequestURI());
        String         server     = parameters.getFromEnd(3);
        String         app        = parameters.getFromEnd(2);
        String         queue      = parameters.getFromEnd(1);
        String         filename   = parameters.getLast();

        CreatedFile files    = createLogFile(server, app, queue, filename);
        File        tempFile = files.tempFile;
        File        logFile  = files.logFile;

        try {
            writeToFile(tempFile, aRequest);
        } catch (IOException e) {
            writeError(aRequest, aResponse, e, tempFile);
        }

        if (!tempFile.renameTo(logFile)) {
            LOG.error("Cannot rename file {} to {}", tempFile.getAbsolutePath(), logFile.getAbsolutePath());
            if (!tempFile.delete()) {
                LOG.error("Cannot delete file {}", tempFile.getAbsolutePath());
            }
        }

    }

    private static void writeError(HttpServletRequest aRequest, HttpServletResponse aResponse, IOException e, File file) throws IOException {
        String errorId = UUID.randomUUID().toString();
        LOG.error("Cannot write from {} to {}, error id = {}", aRequest.getRequestURI(), file.getAbsolutePath(), errorId, e);
        aResponse.setStatus(500);
        aResponse.getWriter().println("""
                {
                  "errorId" : "{{ ERROR_ID }}"
                }
                """.replace("{{ ERROR_ID }}", errorId));
    }

    private void writeToFile(File aFile, HttpServletRequest aRequest) throws IOException {
        try (ServletInputStream in = aRequest.getInputStream(); FileOutputStream out = new FileOutputStream(aFile)) {
            byte[] buf = new byte[4096];
            int    count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
        }
    }

    private CreatedFile createLogFile(String server, String app, String queue, String filename) {
        lock.lock();
        try {
            File queueDir = createDirs(baseDir, server, app, queue);
            File tempFile = new File(queueDir, filename + "-" + System.currentTimeMillis() + ".tmp");
            File logFile  = new File(queueDir, filename);

            if (logFile.exists()) {
                logFile = new File(queueDir, filename + "-" + System.currentTimeMillis());
            }

            return new CreatedFile(tempFile, logFile);
        } finally {
            lock.unlock();
        }
    }
}
