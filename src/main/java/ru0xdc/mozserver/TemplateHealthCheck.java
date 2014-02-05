package ru0xdc.mozserver;

import com.yammer.metrics.core.HealthCheck;

/**
 * Created by alexey on 03.02.14.
 */
public class TemplateHealthCheck extends HealthCheck {
    private final String template = "Hello, %s";

    public TemplateHealthCheck() {
        super("template");
    }

    @Override
    protected HealthCheck.Result check() throws Exception {
        final String saying = String.format(template, "TEST");
        if (!saying.contains("TEST")) {
            return HealthCheck.Result.unhealthy("template doesn't include a name");
        }
        return HealthCheck.Result.healthy();
    }
}
