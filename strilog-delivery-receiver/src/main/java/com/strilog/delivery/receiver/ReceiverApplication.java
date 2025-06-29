package com.strilog.delivery.receiver;

import com.payneteasy.jetty.util.*;
import com.payneteasy.mini.core.app.AppContext;
import com.strilog.delivery.receiver.servlet.FileReceiverServlet;
import com.strilog.delivery.receiver.servlet.PreventStackTraceErrorFilter;

import static com.payneteasy.mini.core.app.AppRunner.runApp;
import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class ReceiverApplication {

    public static void main(String[] args) {
        runApp(args, ReceiverApplication::run);
    }

    private static void run(AppContext aContext) {
        IStartupConfig  config          = getStartupParameters(IStartupConfig.class);
        ReceiverFactory receiverFactory = new ReceiverFactory(config.getDatabaseDir());

        JettyServer jetty = new JettyServerBuilder()
                .startupParameters(config)
                .contextOption(JettyContextOption.SESSIONS)

                .servlet("/health", new HealthServlet() )

                .filter("/*"    , new PreventStackTraceErrorFilter())

                .servlet("/*"    , new FileReceiverServlet(config.getDatabaseDir()))

                .build();

        jetty.startJetty();

    }

}
