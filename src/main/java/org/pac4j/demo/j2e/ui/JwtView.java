package org.pac4j.demo.j2e.ui;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.demo.j2e.SecurityConfig;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * Generate a JWT token.
 */
@Named
@RequestScoped
public class JwtView {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(JwtView.class);

    /** The Pac4j profile manager. */
    @Inject
    private ProfileManager profileManager;

    public String generate() {
        final Optional<CommonProfile> profile = profileManager.get(true);
        final JwtGenerator generator = new JwtGenerator(new SecretSignatureConfiguration(SecurityConfig.JWT_SALT));
        String token = "";
        if (profile.isPresent()) {
            token = generator.generate(profile.get());
        }
        logger.debug("Computed token: " + token);
        return token;
    }
}
