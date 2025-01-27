package io.repliforce.RepliforceJsonValidator.service;

import io.repliforce.RepliforceJsonValidator.domain.Role;
import io.repliforce.RepliforceJsonValidator.domain.User;
import io.repliforce.RepliforceJsonValidator.repositories.RoleRepository;
import io.repliforce.RepliforceJsonValidator.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRolename()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving new user {} to the database...", user.getName());
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database...", role.getRolename());
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String rolename) {
        log.info("Adding role {} to user {} ...", rolename, username);
        User user = userRepository.findByUsername(username);
        Role roleToAdd = roleRepository.findByRolename(rolename);
        user.getRoles().add(roleToAdd);
    }

    @Override
    public User getUser(String username) {
        log.info("Fetching user {}...", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        log.info("Fetching all users...");
        return userRepository.findAll();
    }
}
