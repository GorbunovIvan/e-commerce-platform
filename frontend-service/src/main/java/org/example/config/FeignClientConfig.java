package org.example.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Configuration
@EnableFeignClients(basePackages = "org.example.repository")
public class FeignClientConfig {

    @Value("${spring.security.oauth2.client.registration.keycloak-user-service.client-id}")
    private String clientIdUserService;

    @Value("${spring.security.oauth2.client.registration.keycloak-product-service.client-id}")
    private String clientIdProductService;

    @Bean
    @ConditionalOnProperty(name = "user-service.enabled", havingValue = "true", matchIfMissing = true)
    public RequestInterceptor oauth2FeignRequestInterceptorUserService(OAuth2AuthorizedClientManager clientManager) {
        return requestTemplate -> {
            var authorizedClient = clientManager.authorize(
                    OAuth2AuthorizeRequest.withClientRegistrationId("keycloak-user-service")
                            .principal(clientIdUserService)
                            .build()
            );
            if (authorizedClient != null) {
                var accessToken = authorizedClient.getAccessToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + accessToken);
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = "product-service.enabled", havingValue = "true", matchIfMissing = true)
    public RequestInterceptor oauth2FeignRequestInterceptorProductService(OAuth2AuthorizedClientManager clientManager) {
        return requestTemplate -> {
            var authorizedClient = clientManager.authorize(
                    OAuth2AuthorizeRequest.withClientRegistrationId("keycloak-product-service")
                            .principal(clientIdProductService)
                            .build()
            );
            if (authorizedClient != null) {
                var accessToken = authorizedClient.getAccessToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + accessToken);
            }
        };
    }
}
