package com.strilog.delivery.receiver;

import com.payneteasy.jetty.util.IJettyStartupParameters;
import com.payneteasy.startup.parameters.AStartupParameter;

import java.io.File;

public interface IStartupConfig extends IJettyStartupParameters {

    @Override
    @AStartupParameter(name = "JETTY_CONTEXT", value = "/delivery-receiver")
    String getJettyContext();

    @Override
    @AStartupParameter(name = "JETTY_PORT", value = "8087")
    int getJettyPort();

    @AStartupParameter(name = "RECEIVER_DIR", value = "./target/database")
    File getDatabaseDir();


}
