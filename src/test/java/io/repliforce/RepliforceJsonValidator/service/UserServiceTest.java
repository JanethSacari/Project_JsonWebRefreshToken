package io.repliforce.RepliforceJsonValidator.service;

import io.repliforce.RepliforceJsonValidator.domain.Role;
import io.repliforce.RepliforceJsonValidator.domain.User;
import io.repliforce.RepliforceJsonValidator.repositories.RoleRepository;
import io.repliforce.RepliforceJsonValidator.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser);
        assertEquals("encoded_password", savedUser.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSaveRole() {
        Role role = new Role();
        role.setRolename("ROLE_USER");

        when(roleRepository.save(role)).thenReturn(role);

        Role savedRole = userService.saveRole(role);

        assertNotNull(savedRole);
        assertEquals("ROLE_USER", savedRole.getRolename());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testAddRoleToUser() {
        User user = new User();
        user.setUsername("john_doe");
        Role role = new Role();
        role.setRolename("ROLE_USER");

        when(userRepository.findByUsername("john_doe")).thenReturn(user);
        when(roleRepository.findByRolename("ROLE_USER")).thenReturn(role);

        userService.addRoleToUser("john_doe", "ROLE_USER");

        assertTrue(user.getRoles().contains(role));
        verify(userRepository, times(1)).findByUsername("john_doe");
        verify(roleRepository, times(1)).findByRolename("ROLE_USER");
    }

    @Test
    void testGetUser() {
        User user = new User();
        user.setUsername("john_doe");

        when(userRepository.findByUsername("john_doe")).thenReturn(user);

        User foundUser = userService.getUser("john_doe");

        assertNotNull(foundUser);
        assertEquals("john_doe", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("john_doe");
    }

    @Test
    void testGetUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());

        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.getUsers();

        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("password");
        Role role = new Role();
        role.setRolename("ROLE_USER");
        user.setRoles(List.of(role));

        when(userRepository.findByUsername("john_doe")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("john_doe");

        assertNotNull(userDetails);
        assertEquals("john_doe", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        verify(userRepository, times(1)).findByUsername("john_doe");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknown_user")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknown_user");
        });

        assertEquals("User not found in the database", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown_user");
    }
}