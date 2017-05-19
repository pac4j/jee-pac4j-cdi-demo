package org.pac4j.demo.j2e.listeners;


import org.pac4j.demo.j2e.annotations.DestroyedLiteral;
import org.pac4j.demo.j2e.annotations.InitializedLiteral;
import org.pac4j.demo.j2e.annotations.Pac4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


/**
 * Listens to servlet context events and broadcasts them on the CDI event bus.
 *
 * @author Phillip Ross
 */
@WebListener
public class ServletContextEventBroadcaster implements ServletContextListener {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(ServletContextEventBroadcaster.class);

    /** The servlet context event. */
    @Inject
    @Pac4j
    Event<ServletContext> event;


    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        logger.debug("***************** servlet context initialized");
        event.select(InitializedLiteral.INSTANCE).fire(sce.getServletContext());
    }


    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        logger.debug("***************** servlet context destroyed");
        event.select(DestroyedLiteral.INSTANCE).fire(sce.getServletContext());
    }


}
