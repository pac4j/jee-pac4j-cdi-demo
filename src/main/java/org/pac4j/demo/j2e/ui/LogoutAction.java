package org.pac4j.demo.j2e.ui;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.j2e.util.ProfileView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;


/**
 * Managed bean to handle logout functionality.
 *
 * JSF views such as facelets can reference this to trigger/control logouts.
 *
 * @author Phillip Ross
 */
@Named
@RequestScoped
public class LogoutAction {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(ProfileView.class);

    /** The Pac4j web context. */
    @Inject
    private WebContext webContext;

    /** The Pac4j profile manager. */
    @Inject
    private ProfileManager profileManager;


    /**
     * Initiates a logout process via PAC4J.
     *
     * @return the outcome of the logout action for use by JSF navigation
     */
    public String logout() {
        logger.debug("performing local logout via profile manager.");
        profileManager.logout();
        return "/?faces-redirect=true";
    }


}
