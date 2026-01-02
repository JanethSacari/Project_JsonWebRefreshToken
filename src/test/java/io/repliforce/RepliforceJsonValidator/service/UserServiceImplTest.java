package io.repliforce.RepliforceJsonValidator.service;

import io.repliforce.RepliforceJsonValidator.domain.Role;
import io.repliforce.RepliforceJsonValidator.domain.User;
import io.repliforce.RepliforceJsonValidator.repositories.RoleRepository;
import io.repliforce.RepliforceJsonValidator.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void testLoadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("nosuch")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nosuch"));
    }

    @Test
    void testLoadUserByUsername_userFound_returnsUserDetailsWithAuthorities() {
        User user = new User();
        user.setUsername("tim");
        user.setPassword("encoded");
        Role r = new Role();
        r.setRolename("ROLE_ADMIN");
        user.getRoles().add(r);
        when(userRepository.findByUsername("tim")).thenReturn(user);

        UserDetails ud = userService.loadUserByUsername("tim");
        assertEquals("tim", ud.getUsername());
        assertEquals("encoded", ud.getPassword());
        assertTrue(ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testSaveUser_encodesPassword_andSaves() {
        User u = new User();
        u.setName("Tester");
        u.setUsername("tester");
        u.setPassword("plain");

        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.saveUser(u);
        assertEquals("encoded", saved.getPassword());
        verify(passwordEncoder, times(1)).encode("plain");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAddRoleToUser_addsRoleToUserCollection() {
        User u = new User();
        u.setUsername("anna");
        u.setRoles(new ArrayList<>());
        Role role = new Role();
        role.setRolename("ROLE_X");

        when(userRepository.findByUsername("anna")).thenReturn(u);
        when(roleRepository.findByRolename("ROLE_X")).thenReturn(role);

        userService.addRoleToUser("anna", "ROLE_X");
        assertTrue(u.getRoles().stream().anyMatch(r -> r.getRolename().equals("ROLE_X")));
    }

    @Test
    void testGetUsers_callsRepositoryFindAll_returnsList() {
        List<User> list = new ArrayList<>();
        list.add(new User());
        when(userRepository.findAll()).thenReturn(list);
        assertEquals(1, userService.getUsers().size());
    }
}

