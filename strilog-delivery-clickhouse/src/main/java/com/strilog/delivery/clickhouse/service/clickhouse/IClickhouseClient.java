package com.strilog.delivery.clickhouse.service.clickhouse;

import com.strilog.delivery.clickhouse.service.clickhouse.model.ClickhouseInsertFile;

import java.io.File;

public interface IClickhouseClient {

    void insertFile(File aFile);

}
