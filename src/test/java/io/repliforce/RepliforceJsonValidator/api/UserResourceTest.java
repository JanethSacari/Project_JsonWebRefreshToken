package io.repliforce.RepliforceJsonValidator.api;

import io.repliforce.RepliforceJsonValidator.domain.User;
import io.repliforce.RepliforceJsonValidator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserResourceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserResource userResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();
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
}