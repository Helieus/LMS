package com.ilias.library.repository;

import com.ilias.library.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find a Role entity by its name.
     *
     * @param name the name of the role (e.g., ADMIN, MEMBER)
     * @return the Role entity if found, otherwise null
     */
    Role findByName(String name);

    /**
     * Check if a Role entity with the given name exists.
     *
     * @param name the name of the role
     * @return true if the role exists, otherwise false
     */
    boolean existsByName(String name);
}
