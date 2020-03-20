package org.pac4j.demo.jee;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.config.Config;
import org.pac4j.jee.filter.AbstractConfigFilter;
import org.pac4j.saml.client.SAML2Client;

/**
 * This filter prints the SP metadata.
 * 
 * @author Michael Remond
 */
public class Saml2MetadataFilter extends AbstractConfigFilter {

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) throws IOException {

        SAML2Client client = (SAML2Client) Config.INSTANCE.getClients().findClient("SAML2Client").get();
        client.init();
        response.getWriter().write(client.getServiceProviderMetadataResolver().getMetadata());
        response.getWriter().flush();
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
