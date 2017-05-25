package org.pac4j.demo.j2e.ui;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class FormView {

    private static final Logger logger = LoggerFactory.getLogger(FormView.class);

    @Inject
    private Config config;

    public FormView() {
    }

    public String getCallbackUrl() {
        final String callbackUrl = ((IndirectClient) config.getClients().findClient("FormClient")).getCallbackUrl();
        logger.info("Computed callbackUrl: {}", callbackUrl);
        return callbackUrl;
    }
}
