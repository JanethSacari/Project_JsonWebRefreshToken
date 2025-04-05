package io.repliforce.RepliforceJsonValidator.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.repliforce.RepliforceJsonValidator.domain.Role;
import io.repliforce.RepliforceJsonValidator.domain.User;
import io.repliforce.RepliforceJsonValidator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserResourceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserResource userResource;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void testGetUsers() {
        User user1 = new User();
        user1.setUsername("john_doe");
        User user2 = new User();
        user2.setUsername("jane_doe");

        when(userService.getUsers()).thenReturn(Arrays.asList(user1, user2));

        ResponseEntity<List<User>> response = userResource.getUsers();

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getUsers();
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setUsername("john_doe");

        when(userService.saveUser(user)).thenReturn(user);

        ResponseEntity<User> response = userResource.saveUser(user);

        assertNotNull(response);
        assertEquals("john_doe", response.getBody().getUsername());
        verify(userService, times(1)).saveUser(user);
    }

    @Test
    void testSaveRole() {
        Role role = new Role();
        role.setRolename("ROLE_USER");

        when(userService.saveRole(role)).thenReturn(role);

        ResponseEntity<Role> response = userResource.saveRole(role);

        assertNotNull(response);
        assertEquals("ROLE_USER", response.getBody().getRolename());
        verify(userService, times(1)).saveRole(role);
    }

    @Test
    void testRefreshToken() throws Exception {
        String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huX2RvZSIsImV4cCI6MTYzMjQ5ODQwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        String accessToken = "new_access_token";
        String username = "john_doe";
        String authorizationHeader = "Bearer " + refreshToken;

        User user = new User();
        Role role = new Role();
        role.setRolename("ROLE_USER");
        user.setUsername(username);
        user.setRoles(Collections.singletonList(role));

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        DecodedJWT decodedJWT = null;

        try {
            decodedJWT = JWT.decode(refreshToken);
        } catch (Exception e) {
            fail("Failed to decode JWT: " + e.getMessage());
        }

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(userService.getUser(username)).thenReturn(user);

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        System.out.println("Authorization Header: " + request.getHeader("Authorization"));
        if (decodedJWT != null) {
            System.out.println("Decoded JWT Subject: " + decodedJWT.getSubject());
        }

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        userResource.refreshToken(request, mockResponse);

        verify(request, atLeastOnce()).getHeader("Authorization");

        assertEquals(MediaType.APPLICATION_JSON_VALUE, mockResponse.getContentType());
    }

    @Test
    void testAddRoleToUser() {
        RoleToUserForm form = new RoleToUserForm();
        form.setUsername("john_doe");
        form.setRolename("ROLE_USER");

        doNothing().when(userService).addRoleToUser(form.getUsername(), form.getRolename());

        ResponseEntity<?> response = userResource.addRoleToUser(form);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(userService, times(1)).addRoleToUser(form.getUsername(), form.getRolename());
    }

    @Test
    void getUsersReturnsListOfUsers() {
        User user1 = new User();
        user1.setUsername("john_doe");
        User user2 = new User();
        user2.setUsername("jane_doe");

        when(userService.getUsers()).thenReturn(Arrays.asList(user1, user2));

        ResponseEntity<List<User>> response = userResource.getUsers();

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getUsers();
    }

    @Test
    void saveUserReturnsCreatedUser() {
        User user = new User();
        user.setUsername("john_doe");

        when(userService.saveUser(user)).thenReturn(user);

        ResponseEntity<User> response = userResource.saveUser(user);

        assertNotNull(response);
        assertEquals("john_doe", response.getBody().getUsername());
        verify(userService, times(1)).saveUser(user);
    }

    @Test
    void saveRoleReturnsCreatedRole() {
        Role role = new Role();
        role.setRolename("ROLE_USER");

        when(userService.saveRole(role)).thenReturn(role);

        ResponseEntity<Role> response = userResource.saveRole(role);

        assertNotNull(response);
        assertEquals("ROLE_USER", response.getBody().getRolename());
        verify(userService, times(1)).saveRole(role);
    }

    @Test
    void refreshTokenThrowsExceptionWhenTokenIsMissing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        assertThrows(RuntimeException.class, () -> {
            userResource.refreshToken(request, mockResponse);
        });
    }

    @Test
    void addRoleToUserAddsRoleSuccessfully() {
        RoleToUserForm form = new RoleToUserForm();
        form.setUsername("john_doe");
        form.setRolename("ROLE_USER");

        doNothing().when(userService).addRoleToUser(form.getUsername(), form.getRolename());

        ResponseEntity<?> response = userResource.addRoleToUser(form);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(userService, times(1)).addRoleToUser(form.getUsername(), form.getRolename());
    }
}