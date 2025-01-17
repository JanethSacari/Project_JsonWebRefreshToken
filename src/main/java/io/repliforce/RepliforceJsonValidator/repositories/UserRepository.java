package io.repliforce.RepliforceJsonValidator.repositories;

import io.repliforce.RepliforceJsonValidator.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername (String username);
}