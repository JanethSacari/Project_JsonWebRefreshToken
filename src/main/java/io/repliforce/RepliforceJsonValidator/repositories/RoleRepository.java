package io.repliforce.RepliforceJsonValidator.repositories;

import io.repliforce.RepliforceJsonValidator.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRolename (String rolename);
}
