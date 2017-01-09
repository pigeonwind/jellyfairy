package com.jerry.gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.function.Function;

/**
 * Created by jerryDev on 2017. 1. 7..
 */
public class WASServerLogMappingData {
    private final StringProperty serverName;
    private final StringProperty logDirectory;
    private final StringProperty nameFilterPattern;

    public WASServerLogMappingData(String serverName, String logDirectory, String nameFilterPattern) {
        this.serverName = new SimpleStringProperty( serverName );
        this.logDirectory = new SimpleStringProperty( logDirectory );
        this.nameFilterPattern = new SimpleStringProperty( nameFilterPattern );
    }

    public String getServerName() {
        return serverName.get();
    }

    public StringProperty serverNameProperty() {
        return serverName;
    }

    public String getLogDirectory() {
        return logDirectory.get();
    }

    public StringProperty logDirectoryProperty() {
        return logDirectory;
    }

    public String getNameFilterPattern() {
        return nameFilterPattern.get();
    }

    public StringProperty nameFilterPatternProperty() {
        return nameFilterPattern;
    }
}
