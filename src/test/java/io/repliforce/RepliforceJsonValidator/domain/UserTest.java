package io.repliforce.RepliforceJsonValidator.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Collections;

public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User(1L, "John Doe", "johndoe", "password", Collections.emptyList());

        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("johndoe", user.getUsername());
        assertEquals("password", user.getPassword());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    public void testUserWithRoles() {
        Role roleAdmin = new Role(1L, "ROLE_ADMIN");
        Role roleUser = new Role(2L, "ROLE_USER");

        User user = new User(2L, "Jane Doe", "janedoe", "securepass", Arrays.asList(roleAdmin, roleUser));

        assertEquals(2L, user.getId());
        assertEquals("Jane Doe", user.getName());
        assertEquals("janedoe", user.getUsername());
        assertEquals("securepass", user.getPassword());
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(roleAdmin));
        assertTrue(user.getRoles().contains(roleUser));
    }

    @Test
    public void testSetters() {
        User user = new User();
        user.setId(3L);
        user.setName("Alice Doe");
        user.setUsername("alicedoe");
        user.setPassword("mypassword");

        assertEquals(3L, user.getId());
        assertEquals("Alice Doe", user.getName());
        assertEquals("alicedoe", user.getUsername());
        assertEquals("mypassword", user.getPassword());
    }
}
