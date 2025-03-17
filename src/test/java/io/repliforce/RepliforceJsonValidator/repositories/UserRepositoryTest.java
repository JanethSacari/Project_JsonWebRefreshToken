package io.repliforce.RepliforceJsonValidator.repositories;

import io.repliforce.RepliforceJsonValidator.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
    }

    @Test
    public void testFindByUsername() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");

        when(userRepository.findByUsername("john_doe")).thenReturn(user);

        User foundUser = userRepository.findByUsername("john_doe");

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        assertEquals("john_doe", foundUser.getUsername());

        verify(userRepository, times(1)).findByUsername("john_doe");
    }

    @Test
    public void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("jane_doe")).thenReturn(null);

        User foundUser = userRepository.findByUsername("jane_doe");

        assertNull(foundUser);

        verify(userRepository, times(1)).findByUsername("jane_doe");
    }
}