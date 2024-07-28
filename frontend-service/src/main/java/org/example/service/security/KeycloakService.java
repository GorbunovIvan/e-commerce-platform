package org.example.service.security;

import org.keycloak.common.util.KeycloakUriBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakService {

    @Value("${server.url}")
    private String serverURl;

    @Value("${keycloak.server-url}")
    private String kcServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.web-registration-name}")
    private String registrationName;

    @Value("${spring.security.oauth2.client.registration.keycloak-web.client-id}")
    private String clientId;

    public String getLoginURL() {
        return KeycloakUriBuilder.fromUri(kcServerUrl)
                .path("/realms/{realm-name}/protocol/openid-connect/auth")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile")
                .queryParam("redirect_uri", String.format("%s/oauth2/authorization/%s", serverURl, registrationName))
                .build(realm)
                .toString();
    }

    public String getRegistrationURL() {
        return KeycloakUriBuilder.fromUri(kcServerUrl)
                .path("/realms/{realm-name}/protocol/openid-connect/registrations")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile")
                .queryParam("redirect_uri", String.format("%s/auth/after-registration", serverURl))
                .build(realm)
                .toString();
    }

    public String getLogoutURL() {
        return KeycloakUriBuilder.fromUri(kcServerUrl)
                .path("/realms/{realm-name}/protocol/openid-connect/logout")
                .queryParam("client_id", clientId)
                .queryParam("post_logout_redirect_uri", String.format("%s/logout", serverURl))
                .build(realm)
                .toString();
    }
}
