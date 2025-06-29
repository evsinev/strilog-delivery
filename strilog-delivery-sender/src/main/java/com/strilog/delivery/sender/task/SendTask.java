package com.strilog.delivery.sender.task;

import com.strilog.delivery.core.ThreadCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.Duration.ofSeconds;

public class SendTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SendTask.class);

    private final SendDir sendDir;

    public SendTask(SendDir sendDir) {
        this.sendDir = sendDir;
    }

    @Override
    public void run() {
        ThreadCondition condition = new ThreadCondition(ofSeconds(10), LOG);

        do {
            try {
                int count = sendDir.sendDir();
                LOG.debug("Sent {} files", count);
            } catch (Exception e) {
                LOG.error("Cannot send dir", e);
            }
        } while (condition.awaitCanRun());
    }
}
