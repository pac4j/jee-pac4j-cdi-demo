package org.pac4j.demo.j2e;


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


@Named
@RequestScoped
public class ProfileView {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(ProfileView.class);

    @Inject @Pac4j
    private WebContext webContext;

    @Inject @Pac4j
    private ProfileManager profileManager;


    public ProfileView() {
    }


    public List getProfiles() {
        return profileManager.getAll(true);
    }


    @PostConstruct
    public void init() {
        logger.debug("webContext is null? {}", (webContext == null));
        logger.debug("profileManager is null? {}", (profileManager == null));
    }


}
