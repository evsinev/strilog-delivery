package com.strilog.delivery.sender;

import com.strilog.delivery.core.ElapsedTime;
import com.strilog.delivery.sender.config.IConfigService;
import com.strilog.delivery.sender.config.impl.ConfigServiceImpl;
import com.strilog.delivery.sender.config.model.TSenderConfig;
import com.strilog.delivery.sender.config.model.TSenderConfigGroup;
import com.strilog.delivery.sender.config.model.TSenderConfigGroupDir;
import com.strilog.delivery.sender.task.SendDir;
import com.strilog.delivery.sender.task.SendTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;

public class SenderApplication {

    private static final Logger LOG = LoggerFactory.getLogger( SenderApplication.class );

    public static void main(String[] args) throws UnknownHostException {
        if (args.length != 1) {
            System.err.println("Usage: SenderApplication <config-file>");
            System.exit(1);
        }

        String         hostname      = InetAddress.getLocalHost().getHostName();
        IConfigService configService = new ConfigServiceImpl(new File(args[0]));

        while (!Thread.currentThread().isInterrupted()) {
            TSenderConfig config = configService.loadConfig();
            for (TSenderConfigGroup group : config.getGroups()) {
                String authHeader = getBasicAuthenticationHeader(group.getBasicUsername(), group.getBasicPassword());

                for (TSenderConfigGroupDir dir : group.getDirs()) {
                    String baseUrl = group.getBaseUrl() + "/" + hostname + "/" + dir.getApp() + "/" + dir.getQueue();

                    SendDir sendDir = new SendDir(
                              new File(dir.getDir())
                            , baseUrl
                            , authHeader
                    );

                    ElapsedTime elapsedTime = new ElapsedTime();
                    int         count       = sendDir.sendDir();

                    if (count > 0) {
                        LOG.debug("Sent {} files from {} in {}ms", count, dir.getDir(), elapsedTime.getElapsedMillis());
                    } else {
                        LOG.debug("No files to send from {}", dir.getDir());
                    }
                }
            }

            sleep10Seconds();
        }
    }

    private static void sleep10Seconds() {
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            LOG.error("Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

}
