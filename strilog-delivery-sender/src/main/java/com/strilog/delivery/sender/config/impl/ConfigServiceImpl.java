package com.strilog.delivery.sender.config.impl;

import com.payneteasy.yaml2json.YamlParser;
import com.strilog.delivery.sender.config.IConfigService;
import com.strilog.delivery.sender.config.model.TSenderConfig;

import java.io.File;

public class ConfigServiceImpl implements IConfigService {

    private final YamlParser yamlParser = new YamlParser();

    private final File configFile;

    public ConfigServiceImpl(File configFile) {
        this.configFile = configFile;
    }

    @Override
    public TSenderConfig loadConfig() {
        return yamlParser.parseFile(configFile, TSenderConfig.class);
    }
}
