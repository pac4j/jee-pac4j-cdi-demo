package org.pac4j.demo.j2e;

import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.client.direct.ParameterClient;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.client.indirect.IndirectBasicAuthClient;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.oidc.client.GoogleOidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Pac4J configuration used for demonstration and experimentation.
 *
 * @author Phillip Ross
 */
@Dependent
public class SecurityConfig {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public static final String JWT_SALT = "12345678901234567890123456789012";

    /**
     * Build the Pac4J-specific configuration.
     *
     * @return a Pac4J config containing clients, authorizers, etc
     */
    @Produces @ApplicationScoped
    private Config buildConfiguration() {
        logger.debug("building Security configuration...");

        // Google OIDC configuration/client
        final OidcConfiguration oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setClientId("167480702619-8e1lo80dnu8bpk3k0lvvj27noin97vu9.apps.googleusercontent.com");
        oidcConfiguration.setSecret("MhMme_Ik6IH2JMnAT6MFIfee");
        oidcConfiguration.setUseNonce(true);
        //oidcClient.setPreferredJwsAlgorithm(JWSAlgorithm.RS256);
        oidcConfiguration.addCustomParam("prompt", "consent");
        final GoogleOidcClient oidcClient = new GoogleOidcClient(oidcConfiguration);
        oidcClient.setAuthorizationGenerator((ctx, profile) -> { profile.addRole("ROLE_ADMIN"); return profile; });

        final FormClient formClient = new FormClient(
                "http://localhost:8080/loginForm.action",
                new SimpleTestUsernamePasswordAuthenticator()
        );

        final FormClient jsfFormClient = new FormClient(
                "http://localhost:8080/jsfLoginForm.action",
                new SimpleTestUsernamePasswordAuthenticator()
        );
        jsfFormClient.setName("jsfFormClient");

        final SAML2ClientConfiguration cfg = new SAML2ClientConfiguration("resource:samlKeystore.jks",
                "pac4j-demo-passwd",
                "pac4j-demo-passwd",
                "resource:testshib-providers.xml");
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("http://localhost:8080/callback?client_name=SAML2Client");
        cfg.setServiceProviderMetadataPath(new File("sp-metadata.xml").getAbsolutePath());
        final SAML2Client saml2Client = new SAML2Client(cfg);

        final FacebookClient facebookClient = new FacebookClient("145278422258960", "be21409ba8f39b5dae2a7de525484da8");
        facebookClient.setScope("user_likes,user_birthday,email,user_hometown,user_location");
        final TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA", "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs");
        // HTTP
        final IndirectBasicAuthClient indirectBasicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());

        // CAS
        final CasConfiguration configuration = new CasConfiguration("https://casserverpac4j.herokuapp.com/login");
        //final CasConfiguration configuration = new CasConfiguration("http://localhost:8888/cas/login");
        final CasProxyReceptor casProxy = new CasProxyReceptor();
        //configuration.setProxyReceptor(casProxy);
        final CasClient casClient = new CasClient(configuration);

        // REST authent with JWT for a token passed in the url as the token parameter
        final List<SignatureConfiguration> signatures = new ArrayList<>();
        signatures.add(new SecretSignatureConfiguration(JWT_SALT));
        ParameterClient parameterClient = new ParameterClient("token", new JwtAuthenticator(signatures));
        parameterClient.setSupportGetRequest(true);
        parameterClient.setSupportPostRequest(false);

        // basic auth
        final DirectBasicAuthClient directBasicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());

        final Clients clients = new Clients(
                "http://localhost:8080/callback",
                oidcClient,
                formClient,
                jsfFormClient,
                saml2Client, facebookClient, twitterClient, indirectBasicAuthClient, casClient,
                parameterClient, directBasicAuthClient, new AnonymousClient(), casProxy
        );

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        config.addAuthorizer("mustBeAnon", new IsAnonymousAuthorizer<>("/?mustBeAnon"));
        config.addAuthorizer("mustBeAuth", new IsAuthenticatedAuthorizer<>("/?mustBeAuth"));
        config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected\\.action$"));
        return config;
    }
}
