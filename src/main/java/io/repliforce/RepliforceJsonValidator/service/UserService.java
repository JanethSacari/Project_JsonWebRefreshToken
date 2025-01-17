package io.repliforce.RepliforceJsonValidator.service;

import io.repliforce.RepliforceJsonValidator.domain.Role;
import io.repliforce.RepliforceJsonValidator.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
}
