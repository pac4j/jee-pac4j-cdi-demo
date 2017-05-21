package org.pac4j.demo.j2e;

import org.pac4j.core.config.Config;
import org.pac4j.demo.j2e.annotations.Initialized;
import org.pac4j.j2e.filter.CallbackFilter;
import org.pac4j.j2e.filter.LogoutFilter;
import org.pac4j.j2e.filter.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

/**
 * Pac4J configuration used for demonstration and experimentation.
 *
 * @author Phillip Ross
 */
@Named
@ApplicationScoped
public class WebConfig {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Inject
    private Config config;

    /**
     * Programmatically build a Pac4J configuration.
     *
     * @param servletContext the servlet context in which the configuration will apply
     * @return a Pac4j configuration object
     */
    public Config build(@Observes @Initialized ServletContext servletContext) {
        // Only filters for OIDC Google client are built for now.
        logger.debug("building servlet filters...");
        createAndRegisterGoogleOIDCFilter(servletContext, config);
        createAndRegisterCallbackFilter(servletContext, config);
        createAndRegisterLocalLogoutFilter(servletContext, config);
        return config;
    }


    /**
     * Programmatically build and register Pac4J google OIDC servlet filter.
     *
     * @param servletContext the servlet context in which the filter will reside
     */
    private void createAndRegisterGoogleOIDCFilter(final ServletContext servletContext, final Config config) {
        SecurityFilter securityFilter = new SecurityFilter();
        securityFilter.setConfig(config); // This populates the ConfigSingleton which has not been populated yet (true?)

        // Create, register, and map a filter which applies the Google OIDC client to the urls to be authorized by
        // the google OIDC mechanism.
        FilterRegistration.Dynamic oidcFilterRegistration = servletContext.addFilter(
                "OidcFilter",
                securityFilter
        );
        oidcFilterRegistration.setInitParameter("clients", "GoogleOidcClient");
        oidcFilterRegistration.setInitParameter("authorizers", "securityHeaders");
        oidcFilterRegistration.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST),
                true,  // When this is true... declared mappings take precedence over this dynamic mapping
                "/oidc/*"
        );
    }


    /**
     * Programmatically build and register Pac4J callback servlet filter.
     *
     * @param servletContext the servlet context in which the filter will reside
     */
    private void createAndRegisterCallbackFilter(final ServletContext servletContext, final Config config) {
        CallbackFilter callbackFilter = new CallbackFilter();
        // The following will avoid RE-populating the ConfigSingleton which has already been populated when the
        // security filter config was set (true?)
        callbackFilter.setConfigOnly(config);
        FilterRegistration.Dynamic callbackFilterRegistration = servletContext.addFilter(
                "callbackFilter",
                callbackFilter
        );
        callbackFilterRegistration.setInitParameter("defaultUrl", "/");
        callbackFilterRegistration.setInitParameter("multiProfile", "true");
        callbackFilterRegistration.setInitParameter("renewSession", "true");
        callbackFilterRegistration.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST),
                true,  // When this is true... declared mappings take precedence over this dynamic mapping
                "/callback"
        );
    }


    /**
     * Programmatically build and register Pac4J local logout servlet filter.
     *
     * @param servletContext the servlet context in which the filter will reside
     */
    private void createAndRegisterLocalLogoutFilter(final ServletContext servletContext, final Config config) {
        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setConfigOnly(config);
        FilterRegistration.Dynamic localLogoutFilterRegistration = servletContext.addFilter(
                "logoutFilter",
                logoutFilter
        );
        localLogoutFilterRegistration.setInitParameter("defaultUrl", "/?defaulturlafterlogout");
        localLogoutFilterRegistration.setInitParameter("killSession", "true");
        localLogoutFilterRegistration.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST),
                true,  // When this is true... declared mappings take precedence over this dynamic mapping
                "/logout"
        );
    }
}
