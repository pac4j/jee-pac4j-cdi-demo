package org.pac4j.demo.j2e;

import org.pac4j.core.config.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class DemoConfig {

    @Produces
    public Config build() {
        // TODO: remove the DemoConfigFactory class and directly have all the configuration inside this class
        return new DemoConfigFactory().build(null);
    }
}
