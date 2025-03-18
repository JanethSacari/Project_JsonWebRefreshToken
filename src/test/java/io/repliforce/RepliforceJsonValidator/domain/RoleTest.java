package io.repliforce.RepliforceJsonValidator.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoleTest {

    @Test
    public void testRoleCreation() {
        Role role = new Role(1L, "ROLE_ADMIN");

        assertEquals(1L, role.getId());
        assertEquals("ROLE_ADMIN", role.getRolename());
    }

    @Test
    public void testNoArgsConstructor() {
        Role role = new Role();

        assertNull(role.getId());
        assertNull(role.getRolename());

        role.setId(2L);
        role.setRolename("ROLE_USER");

        assertEquals(2L, role.getId());
        assertEquals("ROLE_USER", role.getRolename());
    }

    @Test
    public void testAllArgsConstructor() {
        Role role = new Role(3L, "ROLE_MANAGER");

        assertEquals(3L, role.getId());
        assertEquals("ROLE_MANAGER", role.getRolename());
    }
}
