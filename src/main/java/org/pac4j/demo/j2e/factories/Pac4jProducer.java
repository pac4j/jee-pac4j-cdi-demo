package org.pac4j.demo.j2e.factories;


import org.pac4j.core.config.ConfigSingleton;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.demo.j2e.annotations.Pac4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Produces request-scoped web context and profile manager via the Pac4J framework.
 *
 * @author Phillip Ross
 */
@Named
@RequestScoped
public class Pac4jProducer {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(Pac4jProducer.class);


    /**
     * Factory method which produces a Pac4J web context.
     *
     * @param httpServletRequest the http servlet request to be used for building the web context
     * @param httpServletResponse the http servlet response to be used for building the web context
     * @return a Pac4J web context associated with the current servlet request
     */
    @Pac4j
    @Produces
    WebContext getWebContext(final HttpServletRequest httpServletRequest,
                             final HttpServletResponse httpServletResponse) {
        logger.debug("Producing a Pac4J web context...");
        J2EContext j2EContext = new J2EContext(
                httpServletRequest,
                httpServletResponse,
                ConfigSingleton.getConfig().getSessionStore()
        );
        logger.debug("Returning a Pac4J web context.");
        return j2EContext;

    }


    /**
     * Factory method which produces a Pac4J profile manager.
     *
     * @param webContext the web context to be used for building the profile manager
     * @return a Pac4J profile manager associated with the current servlet request
     */
    @Pac4j
    @Produces
    ProfileManager getProfileManager(@Pac4j final WebContext webContext) {
        logger.debug("Producing a Pac4J profile manager...");
        ProfileManager profileManager = new ProfileManager(webContext);
        logger.debug("Returning a Pac4J profile manager.");
        return profileManager;
    }


}
