package ru0xdc.mozserver;

import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;

public class MozserverConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("database")
    private DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

    @Valid
    @JsonProperty("allowCors")
    private boolean allowCors = false;

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public boolean isCorsAllowed() {
        return allowCors;
    }
}
