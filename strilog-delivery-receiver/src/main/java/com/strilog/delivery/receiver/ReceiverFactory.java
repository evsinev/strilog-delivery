package com.strilog.delivery.receiver;

import com.payneteasy.mini.core.context.IServiceContext;
import com.payneteasy.mini.core.context.ServiceContextImpl;

import java.io.File;

public class ReceiverFactory {

    private final IServiceContext context = new ServiceContextImpl();
    private final File            databaseDir;

    public ReceiverFactory(File databaseDir) {
        this.databaseDir = databaseDir;
    }
}
