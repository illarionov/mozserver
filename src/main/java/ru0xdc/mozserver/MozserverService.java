package ru0xdc.mozserver;

import com.google.common.collect.Maps;
import com.palominolabs.jersey.cors.Cors;
import com.palominolabs.jersey.cors.CorsResourceFilterFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import com.yammer.dropwizard.migrations.MigrationsBundle;
import org.skife.jdbi.v2.DBI;
import ru0xdc.mozserver.api.v1.CellCoverage;
import ru0xdc.mozserver.api.v1.CellResource;
import ru0xdc.mozserver.api.v1.RefreshCoverageTask;
import ru0xdc.mozserver.api.v1.SubmitResource;
import ru0xdc.mozserver.jdbi.CellDao;
import ru0xdc.mozserver.jdbi.CellLogDao;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MozserverService extends Service<MozserverConfiguration> {

    public static void main(String[] args) throws Exception {
        new MozserverService().run(args);
    }

    @Override
    public void initialize(Bootstrap<MozserverConfiguration> bootstrap) {
        bootstrap.setName("hello-world");
        bootstrap.addBundle(new MigrationsBundle<MozserverConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(MozserverConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });
    }

    @Override
    public void run(MozserverConfiguration configuration,
                    Environment environment) {
        final DBI jdbi;
        final DBIFactory factory = new DBIFactory();


        try {
            jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "postgresql");
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalStateException("factory.build() error", cnfe);
        }

        CellDao cellDao = jdbi.onDemand(CellDao.class);
        CellLogDao cellLogDao = jdbi.onDemand(CellLogDao.class);

        initCors(configuration, environment);
        environment.addResource(new MozserverResource());
        environment.addHealthCheck(new TemplateHealthCheck());
        environment.addResource(new SubmitResource(jdbi));
        environment.addResource(new CellResource(cellDao));
        environment.addResource(new CellCoverage(cellLogDao));
        environment.addResource(new DBIExceptionsBundle());
        environment.addTask(new RefreshCoverageTask(cellLogDao));

    }

    private void initCors(MozserverConfiguration configuration,
                          Environment environment) {

        DefaultResourceConfig config = new DefaultResourceConfig();
        if (!configuration.isCorsAllowed()) {
            Map<String, Object> props = Maps.newHashMap();
            props.put("com.palominolabs.jersey.cors.allowOrigin", "");
            config.setPropertiesAndFeatures(props);
        }

        environment.setJerseyProperty(ResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES,
                Collections.singletonList(new CorsResourceFilterFactory(config))
                );
    }
}