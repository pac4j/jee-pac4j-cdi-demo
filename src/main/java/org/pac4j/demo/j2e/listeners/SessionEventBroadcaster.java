package org.pac4j.demo.j2e.listeners;


import org.pac4j.demo.j2e.annotations.DestroyedLiteral;
import org.pac4j.demo.j2e.annotations.InitializedLiteral;
import org.pac4j.demo.j2e.annotations.Pac4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


/**
 * Listens to session events and broadcasts them on the CDI event bus.
 *
 * @author Phillip Ross
 */
@WebListener
public class SessionEventBroadcaster implements HttpSessionListener {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(SessionEventBroadcaster.class);

    /** The http session event. */
    @Inject
    @Pac4j
    Event<HttpSession> event;

    @Override
    public void sessionCreated(final HttpSessionEvent se) {
        logger.debug("***************** http session created");
        event.select(InitializedLiteral.INSTANCE).fire(se.getSession());
    }


    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        logger.debug("***************** http session destroyed");
        event.select(DestroyedLiteral.INSTANCE).fire(se.getSession());
    }


}
