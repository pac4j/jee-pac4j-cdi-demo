package org.pac4j.demo.jee;

import org.pac4j.core.config.Config;
import org.pac4j.jee.util.FilterHelper;
import org.pac4j.jee.filter.CallbackFilter;
import org.pac4j.jee.filter.LogoutFilter;
import org.pac4j.jee.filter.SecurityFilter;
import org.pac4j.saml.metadata.Saml2MetadataFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

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
     * Programmatically define the web configuration.
     *
     * @param servletContext the servlet context in which the configuration will apply
     */
    public void build(@Observes @Initialized(ApplicationScoped.class) ServletContext servletContext) {
        logger.debug("building Web configuration...");

        final FilterHelper filterHelper = new FilterHelper(servletContext);

        final SecurityFilter indexFilter = new SecurityFilter(config, "AnonymousClient");
        filterHelper.addFilterMapping("indexFilter", indexFilter, "/");

        final SecurityFilter mustBeAnonFilter = new SecurityFilter(config, "AnonymousClient", "mustBeAnon");
        filterHelper.addFilterMapping("mustBeAnonFilter", mustBeAnonFilter, "/loginForm.action");

        final CallbackFilter callbackFilter = new CallbackFilter(config, "/");
        callbackFilter.setRenewSession(true);
        filterHelper.addFilterMapping("callbackFilter", callbackFilter, "/callback");

        final SecurityFilter jwtParameterFilter = new SecurityFilter(config, "ParameterClient");
        filterHelper.addFilterMapping("jwtParameterFilter", jwtParameterFilter, "/rest-jwt/*");

        final SecurityFilter directBasicAuthFilter = new SecurityFilter(config, "DirectBasicAuthClient,ParameterClient");
        filterHelper.addFilterMapping("directBasicAuthFilter", directBasicAuthFilter, "/dba/*");

        final SecurityFilter oidcFilter = new SecurityFilter(config, "GoogleOidcClient");
        filterHelper.addFilterMapping("oidcFilter", oidcFilter, "/oidc/*");

        final ForceLoginFilter forceLoginFilter = new ForceLoginFilter();
        filterHelper.addFilterMapping("forceLoginFilter", forceLoginFilter, "/forceLogin");

        final SecurityFilter saml2Filter = new SecurityFilter(config, "SAML2Client");
        filterHelper.addFilterMapping("saml2Filter", saml2Filter, "/saml2/*");

        final Saml2MetadataFilter saml2MetadataFilter = new Saml2MetadataFilter();
        filterHelper.addFilterMapping("saml2MetadataFilter", saml2MetadataFilter, "/saml2-metadata");

        final SecurityFilter facebookFilter = new SecurityFilter(config, "FacebookClient");
        facebookFilter.setMatchers("excludedPath");
        filterHelper.addFilterMapping("facebookFilter", facebookFilter, "/facebook/*");

        final SecurityFilter protectedFilter = new SecurityFilter(config);
        filterHelper.addFilterMapping("protectedFilter", protectedFilter, "/protected/*");

        final SecurityFilter facebookAdminFilter = new SecurityFilter(config, "FacebookClient", "admin");
        filterHelper.addFilterMapping("facebookAdminFilter", facebookAdminFilter, "/facebookadmin/*");

        final SecurityFilter facebookCustomFilter = new SecurityFilter(config, "FacebookClient", "custom");
        filterHelper.addFilterMapping("facebookCustomFilter", facebookCustomFilter, "/facebookcustom/*");

        final SecurityFilter twitterFilter = new SecurityFilter(config, "TwitterClient,FacebookClient");
        filterHelper.addFilterMapping("twitterFilter", twitterFilter, "/twitter/*");

        final SecurityFilter formFilter = new SecurityFilter(config, "FormClient");
        filterHelper.addFilterMapping("formFilter", formFilter, "/form/*");

        final SecurityFilter jsfFormFilter = new SecurityFilter(config, "jsfFormClient");
        filterHelper.addFilterMapping("jsfFormFilter", jsfFormFilter, "/jsfform/*");

        final SecurityFilter indirectBasicAuthFilter = new SecurityFilter(config, "IndirectBasicAuthClient");
        filterHelper.addFilterMapping("indirectBasicAuthFilter", indirectBasicAuthFilter, "/basicauth/*");

        final SecurityFilter casFilter = new SecurityFilter(config, "CasClient");
        filterHelper.addFilterMapping("casFilter", casFilter, "/cas/*");

        final SecurityFilter mustBeAuthFilter = new SecurityFilter(config, "AnonymousClient", "mustBeAuth");
        filterHelper.addFilterMapping("mustBeAuthFilter", mustBeAuthFilter, "/logout");

        final LogoutFilter logoutFilter = new LogoutFilter(config, "/?defaulturlafterlogout");
        logoutFilter.setDestroySession(true);
        filterHelper.addFilterMapping("logoutFilter", logoutFilter, "/logout");

        final LogoutFilter centralLogoutFilter = new LogoutFilter(config, "http://localhost:8080/?defaulturlafterlogoutafteridp");
        centralLogoutFilter.setLocalLogout(false);
        centralLogoutFilter.setCentralLogout(true);
        centralLogoutFilter.setLogoutUrlPattern("http://localhost:8080/.*");
        filterHelper.addFilterMapping("centralLogoutFilter", centralLogoutFilter, "/centralLogout");
    }
}
