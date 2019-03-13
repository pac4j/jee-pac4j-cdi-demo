package org.pac4j.demo.jee.ui;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class FormView {

    private static final Logger logger = LoggerFactory.getLogger(FormView.class);

    private String username;

    private String password;

    @Inject
    private Config config;

    @Inject
    private ProfileManager profileManager;


    public FormView() {
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(final String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(final String password) {
        this.password = password;
    }


    public String getCallbackUrl() {
        final String callbackUrl = ((IndirectClient) config.getClients().findClient("FormClient").get()).getCallbackUrl();
        logger.info("Computed callbackUrl: {}", callbackUrl);
        return callbackUrl;
    }


    public String authenticate() {
        logger.debug("authenticating username/password: {}/[PROTECTED]", username);
        String outcome = null;
        if (!username.equals(password)) {
            final String message = "Authentication failed.";
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        } else {
            final boolean saveInSession = true;
            final boolean multiProfile = false;
            final CommonProfile profile = new CommonProfile();
            profile.setId(username);
            profile.addAttribute("username", username);
            profileManager.save(saveInSession, profile, multiProfile);
            outcome = "/jsfform/index?faces-redirect=true";
        }
        return outcome;
    }


}
