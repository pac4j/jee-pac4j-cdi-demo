package org.pac4j.demo.jee.ui;

import org.pac4j.cas.profile.CasProxyProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * Generate a CAS proxy ticket.
 */
@Named
@RequestScoped
public class CasView {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(CasView.class);

    /** The Pac4j profile manager. */
    @Inject
    private ProfileManager profileManager;

    public String getProxyTicket() {
        final Optional<CommonProfile> optProfile = profileManager.get(true);
        String pt = null;
        if (optProfile.isPresent()) {
            CommonProfile profile = optProfile.get();
            if (profile instanceof CasProxyProfile) {
                final CasProxyProfile casProxyProfile = (CasProxyProfile) profile;
                pt = casProxyProfile.getProxyTicketFor("http://test");
            }
        }
        logger.debug("Computed PT: " + pt);
        return pt;
    }
}
