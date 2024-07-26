package org.example.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.example.model.User;
import org.example.model.UserDTO;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    private Jwt jwt;

    private final String baseURI = "/api/v1/users";

    @PostConstruct
    void init() {
        objectMapper.findAndRegisterModules();
    }

    @BeforeEach
    public void setUp() {

        // Create a mock JWT token
        jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("iat", Instant.now())
                .claim("exp", Instant.now().plusSeconds(3600))
                .build();

        // Mock the JwtDecoder to return the mock JWT token
        given(jwtDecoder.decode("mock-token")).willReturn(jwt);
    }

    @Test
    void shouldResponseUnauthorizedWhenGetAll() throws Exception {

        mockMvc.perform(get(baseURI))
                .andExpect(status().isForbidden());

        verify(userService, never()).getAll();
    }

    @Test
    void shouldReturnListOfUsersWhenGetAll() throws Exception {

        var usersExpected = List.of(
                new User(1L, "user-1", LocalDate.now().minusDays(3)),
                new User(2L, "user-2", LocalDate.now().minusDays(2)),
                new User(3L, "user-3", LocalDate.now().minusDays(1))
        );

        when(userService.getAll()).thenReturn(usersExpected);

        var jsonResponse = mockMvc.perform(get(baseURI)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<User> users = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(users);
        assertEquals(usersExpected, users);

        verify(userService, times(1)).getAll();
        verify(userService, only()).getAll();
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() throws Exception {

        when(userService.getAll()).thenReturn(Collections.emptyList());

        var jsonResponse = mockMvc.perform(get(baseURI)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<User> users = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(users);
        assertTrue(users.isEmpty());

        verify(userService, times(1)).getAll();
        verify(userService, only()).getAll();
    }

    @Test
    void shouldReturnUserWhenGetById() throws Exception {

        var id = 1L;
        var userExpected = new User(id, "user-by-id", LocalDate.now().minusMonths(5));

        when(userService.getById(id)).thenReturn(userExpected);

        var jsonResponse = mockMvc.perform(get(baseURI + "/{id}", id)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user = objectMapper.readValue(jsonResponse, User.class);

        assertNotNull(user);
        assertEquals(userExpected, user);

        verify(userService, times(1)).getById(1L);
        verify(userService, only()).getById(1L);
    }

    @Test
    void shouldReturnNullWhenGetById() throws Exception {

        var id = 1L;

        mockMvc.perform(get(baseURI + "/{id}", id)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getById(id);
        verify(userService, only()).getById(id);
    }

    @Test
    void shouldReturnListOfUsersWhenGetByIds() throws Exception {

        var usersExpected = List.of(
                new User(1L, "user-1", LocalDate.now().minusDays(3)),
                new User(2L, "user-2", LocalDate.now().minusDays(2)),
                new User(3L, "user-3", LocalDate.now().minusDays(1))
        );

        var ids = usersExpected.stream().map(User::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        var idsParam = ids.stream().map(String::valueOf).collect(Collectors.joining(","));

        when(userService.getByIds(ids)).thenReturn(usersExpected);

        var jsonResponse = mockMvc.perform(get(baseURI + "/ids/{id}", idsParam)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<User> users = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(usersExpected, users);

        verify(userService, times(1)).getByIds(ids);
        verify(userService, only()).getByIds(ids);
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() throws Exception {

        var ids = new LinkedHashSet<>(List.of(1L, 2L, 3L));
        var idsParam = ids.stream().map(String::valueOf).collect(Collectors.joining(","));

        when(userService.getByIds(ids)).thenReturn(Collections.emptyList());

        var jsonResponse = mockMvc.perform(get(baseURI + "/ids/{id}", idsParam)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<User> users = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(users);
        assertTrue(users.isEmpty());

        verify(userService, times(1)).getByIds(ids);
        verify(userService, only()).getByIds(ids);
    }

    @Test
    void shouldReturnUserWhenGetByUsername() throws Exception {

        var username = "username-of-user";
        var userExpected = new User(99L, username, LocalDate.now().minusWeeks(7));

        when(userService.getByUsername(any())).thenReturn(userExpected);

        var jsonResponse = mockMvc.perform(get(baseURI + "/username/{username}", username)
                    .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user = objectMapper.readValue(jsonResponse, User.class);

        assertNotNull(user);
        assertEquals(userExpected, user);

        verify(userService, times(1)).getByUsername(any());
        verify(userService, only()).getByUsername(any());
    }

    @Test
    void shouldReturnNullWhenGetByUsername() throws Exception {

        var username = "username-of-user";

        mockMvc.perform(get(baseURI + "/username/{username}", username)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getByUsername(any());
        verify(userService, only()).getByUsername(any());
    }

    @Test
    void shouldCreateAndReturnNewUserWhenCreate() throws Exception {

        when(userService.create(any(UserDTO.class))).thenAnswer(answer -> {
            UserDTO userDTO = answer.getArgument(0);
            User user = userDTO.toUser();
            user.setId(99L);
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(LocalDate.now());
            }
            return user;
        });

        var userDTO = new UserDTO("new-user");
        var jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        var jsonResponse = mockMvc.perform(post(baseURI)
                        .with(jwt().jwt(jwt))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUserDTO))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user = objectMapper.readValue(jsonResponse, User.class);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertEquals(userDTO.getUsername(), user.getUsername());

        verify(userService, times(1)).create(any(UserDTO.class));
        verify(userService, only()).create(any(UserDTO.class));
    }

    @Test
    void shouldUpdateAndReturnUserWhenUpdate() throws Exception {

        var id = 1L;
        var userExisting = new User(id, "user-1", LocalDate.now());

        when(userService.getById(id)).thenReturn(userExisting);
        when(userService.update(anyLong(), any(UserDTO.class))).thenAnswer(answer -> {
            Long idParam = answer.getArgument(0);
            UserDTO userDTO = answer.getArgument(1);
            var user = userDTO.toUser();
            user.setId(idParam);
            return user;
        });

        var userDTO = new UserDTO("new-username", LocalDate.now().minusDays(10));
        var jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        var jsonResponse = mockMvc.perform(put(baseURI + "/{id}", id)
                        .with(jwt().jwt(jwt))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUserDTO))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User userUpdated = objectMapper.readValue(jsonResponse, User.class);

        assertNotNull(userUpdated);
        assertEquals(id, userUpdated.getId());
        assertEquals(userDTO.getUsername(), userUpdated.getUsername());
        assertEquals(userDTO.getCreatedAt(), userUpdated.getCreatedAt());

        verify(userService, times(1)).update(anyLong(), any(UserDTO.class));
        verify(userService, only()).update(anyLong(), any(UserDTO.class));
    }

    @Test
    void shouldReturnNullWhenUpdate() throws Exception {

        var id = 1L;

        var userDTO = new UserDTO("new-username", LocalDate.now().minusDays(10));
        var jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(put(baseURI + "/{id}", id)
                        .with(jwt().jwt(jwt))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUserDTO))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).update(anyLong(), any(UserDTO.class));
        verify(userService, only()).update(anyLong(), any(UserDTO.class));
    }

    @Test
    void shouldDeleteUserWhenDelete() throws Exception {

        var id = 1L;

        mockMvc.perform(delete(baseURI + "/{id}", id)
                    .with(jwt().jwt(jwt)))
                .andExpect(status().isAccepted());

        verify(userService, times(1)).deleteById(id);
        verify(userService, only()).deleteById(id);
    }
}