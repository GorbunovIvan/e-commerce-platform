package org.example.repository.users.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.example.model.users.User;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WireMockTest
@EnableFeignClients
@TestPropertySource(properties = "user-service.enabled=true")
class UserRepositoryBasedOnFeignClientTest {

    @Autowired
    private UserRepositoryBasedOnFeignClient userRepositoryBasedOnFeignClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom easyRandom = new EasyRandom();

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void setUpMockBaseURL(DynamicPropertyRegistry registry) {

        var remoteServerHost = getHostFromWireMockBaseURL();
        var remoteServerPort = wireMockExtension.getPort();
        var remoteServerAddress = "http://" + remoteServerHost + ":" + remoteServerPort + "/api/v1/users";

        registry.add("user-service.url", () -> remoteServerAddress);
    }

    @BeforeAll
    static void beforeAll() {
        configureFor(getHostFromWireMockBaseURL(), wireMockExtension.getPort());
    }

    private static String getHostFromWireMockBaseURL() {
        try {
            return (new URI(wireMockExtension.baseUrl())).getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturnServiceNameWhenGetServiceName() {
        var serviceNameExpected = "user-service";
        var result = userRepositoryBasedOnFeignClient.getServiceName();
        assertEquals(serviceNameExpected, result);
    }

    @Test
    void shouldReturnListOfUsersWhenGetAll() throws JsonProcessingException {

        var usersExpected = easyRandom.objects(User.class, 3).toList();
        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(usersExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/users"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var usersReceived = userRepositoryBasedOnFeignClient.getAll();
        assertEquals(usersExpected, usersReceived);

        verify(getRequestedFor(urlEqualTo("/api/v1/users")));
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() throws JsonProcessingException {

        var usersExpected = Collections.emptyList();
        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(usersExpected);
        
        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/users"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var users = userRepositoryBasedOnFeignClient.getAll();
        assertNotNull(users);
        assertTrue(users.isEmpty());

        verify(getRequestedFor(urlEqualTo("/api/v1/users")));
    }

    @Test
    void shouldReturnUserWhenGetById() throws JsonProcessingException {

        var userExpected = easyRandom.nextObject(User.class);
        var id = userExpected.getId();

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(userExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/users/" + id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var user = userRepositoryBasedOnFeignClient.getById(id);
        assertNotNull(user);
        assertEquals(userExpected, user);

        verify(getRequestedFor(urlEqualTo("/api/v1/users/" + id)));
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = 1L;

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/users/" + id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)));

        var user = userRepositoryBasedOnFeignClient.getById(id);
        assertNull(user);

        verify(getRequestedFor(urlEqualTo("/api/v1/users/" + id)));
    }

    @Test
    void shouldReturnUsersWhenGetByIds() throws JsonProcessingException {

        var usersExpected = easyRandom.objects(User.class, 3).toList();
        var ids = usersExpected.stream().map(User::getId).collect(Collectors.toSet());

        var idsRequest = ids.stream().map(String::valueOf).collect(Collectors.joining(","));

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(usersExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/users/ids/" + idsRequest))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var users = userRepositoryBasedOnFeignClient.getByIds(ids);
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(usersExpected, users);

        verify(getRequestedFor(urlEqualTo("/api/v1/users/ids/" + idsRequest)));
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() throws JsonProcessingException {

        var usersExpected = Collections.emptyList();
        Set<Long> ids = Collections.emptySet();

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(usersExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/users/ids/"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var users = userRepositoryBasedOnFeignClient.getByIds(ids);
        assertNotNull(users);
        assertTrue(users.isEmpty());

        verify(getRequestedFor(urlEqualTo("/api/v1/users/ids/")));
    }

    @Test
    void shouldReturnUserWhenGetByUsername() throws JsonProcessingException {

        var userExpected = easyRandom.nextObject(User.class);
        var username = userExpected.getUsername();

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(userExpected);

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/users/username/" + username))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var user = userRepositoryBasedOnFeignClient.getByUsername(username);
        assertNotNull(user);
        assertEquals(userExpected, user);

        verify(getRequestedFor(urlEqualTo("/api/v1/users/username/" + username)));
    }

    @Test
    void shouldReturnNullWhenGetByUsername() {

        var username = "username-of-user";

        // Stubbing remote-service
        stubFor(get(urlEqualTo("/api/v1/users/username/" + username))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)));

        var user = userRepositoryBasedOnFeignClient.getByUsername(username);
        assertNull(user);

        verify(getRequestedFor(urlEqualTo("/api/v1/users/username/" + username)));
    }

    @Test
    void shouldCreateAndReturnNewUserWhenCreate() throws JsonProcessingException {

        var userExpected = easyRandom.nextObject(User.class);

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(userExpected);

        // Stubbing remote-service
        stubFor(post(urlEqualTo("/api/v1/users"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var user = userRepositoryBasedOnFeignClient.create(userExpected);
        assertNotNull(user);
        assertEquals(userExpected, user);

        verify(postRequestedFor(urlEqualTo("/api/v1/users")));
    }

    @Test
    void shouldUpdateAndReturnUserWhenUpdate() throws JsonProcessingException {

        var userExisting = easyRandom.nextObject(User.class);
        var id = userExisting.getId();

        var jsonResponseFromRemoteService = objectMapper.writeValueAsString(userExisting);

        // Stubbing remote-service
        stubFor(put(urlEqualTo("/api/v1/users/" + id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(jsonResponseFromRemoteService)));

        var userUpdated = userRepositoryBasedOnFeignClient.update(id, userExisting);
        assertNotNull(userUpdated);
        assertEquals(id, userUpdated.getId());
        assertEquals(userExisting, userUpdated);

        verify(putRequestedFor(urlEqualTo("/api/v1/users/" + id)));
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var id = 1L;

        var user = easyRandom.nextObject(User.class);

        // Stubbing remote-service
        stubFor(put(urlEqualTo("/api/v1/users/" + id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)));

        var userUpdated = userRepositoryBasedOnFeignClient.update(id, user);
        assertNull(userUpdated);

        verify(putRequestedFor(urlEqualTo("/api/v1/users/" + id)));
    }

    @Test
    void shouldDeleteUserWhenDelete() {

        var id = 1L;

        // Stubbing remote-service
        stubFor(delete(urlEqualTo("/api/v1/users/" + id))
                .willReturn(aResponse()
                        .withStatus(200)));

        userRepositoryBasedOnFeignClient.deleteById(id);

        verify(deleteRequestedFor(urlEqualTo("/api/v1/users/" + id)));
    }
}