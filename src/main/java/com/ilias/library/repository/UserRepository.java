package com.ilias.library.repository;

import com.ilias.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsernameAndPassword(String username, String password);

    User findByUsername(String username);

    User findByEmail(String email);
}
