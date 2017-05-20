package org.pac4j.demo.j2e.ui;


import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.demo.j2e.annotations.Pac4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;


/**
 * Managed bean which exposes the Pac4J profile manager.
 *
 * JSF views such as facelets can reference this to view the contents of profiles.
 *
 * @author Phillip Ross
 */
@Named
@RequestScoped
public class ProfileView {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(ProfileView.class);

    /** The Pac4j web context. */
    @Inject @Pac4j
    private WebContext webContext;

    /** The Pac4j profile manager. */
    @Inject @Pac4j
    private ProfileManager profileManager;


    public ProfileView() {
    }


    /**
     * Gets the profiles contained in the profile manager.
     *
     * @return a list of Pac4j profiles
     */
    public List getProfiles() {
        return profileManager.getAll(true);
    }


    /** Simply prints some debugging information post-construction. */
    @PostConstruct
    public void init() {
        logger.debug("webContext is null? {}", (webContext == null));
        logger.debug("profileManager is null? {}", (profileManager == null));
    }


}
