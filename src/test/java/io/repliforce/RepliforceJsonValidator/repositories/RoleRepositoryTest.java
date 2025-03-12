package io.repliforce.RepliforceJsonValidator.repositories;

import io.repliforce.RepliforceJsonValidator.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleRepositoryTest {

    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
    }

    @Test
    public void testFindByRolename() {
        Role role = new Role(1L, "ROLE_ADMIN");

        when(roleRepository.findByRolename("ROLE_ADMIN")).thenReturn(role);

        Role foundRole = roleRepository.findByRolename("ROLE_ADMIN");

        assertNotNull(foundRole);
        assertEquals(1L, foundRole.getId());
        assertEquals("ROLE_ADMIN", foundRole.getRolename());

        verify(roleRepository, times(1)).findByRolename("ROLE_ADMIN");
    }

    @Test
    public void testFindByRolename_NotFound() {
        when(roleRepository.findByRolename("ROLE_USER")).thenReturn(null);

        Role foundRole = roleRepository.findByRolename("ROLE_USER");

        assertNull(foundRole);

        verify(roleRepository, times(1)).findByRolename("ROLE_USER");
    }
}