package org.pac4j.demo.jee;

import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.adapter.JEEHttpActionAdapter;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.jee.filter.AbstractConfigFilter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ForceLoginFilter extends AbstractConfigFilter {

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) {

        final JEEContext context = new JEEContext(request, response);
        final Client client = Config.INSTANCE.getClients().findClient(request.getParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER)).get();
        HttpAction action;
        try {
            action = (HttpAction) client.getRedirectionAction(context).get();
        } catch (final HttpAction e) {
            action = e;
        }
        JEEHttpActionAdapter.INSTANCE.adapt(action, context);
    }
}
