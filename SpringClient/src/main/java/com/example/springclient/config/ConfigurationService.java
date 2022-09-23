package com.example.springclient.config;

import com.example.springclient.controllers.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@RefreshScope
@Service
public class ConfigurationService {
    @Autowired
    private Environment env;

    public String getAuthServerUrl() {
        return env.getProperty("keycloak.auth-server-url");
    }

    public String getRealm() {
        return env.getProperty("keycloak.realm");
    }

    public String getClientId() {
        return env.getProperty("keycloak.resource");
    }

    public String getClientSecret() {
        return env.getProperty("clientSecret");
    }

    public String getMasterRealm() {
        return env.getProperty("master.realm");
    }

    public String getMasterClientId() {
        return env.getProperty("master.clientId");
    }

    public String getMasterUsername() {
        return env.getProperty("master.username");
    }

    public String getMasterPassword() {
        return env.getProperty("master.password");
    }

}
