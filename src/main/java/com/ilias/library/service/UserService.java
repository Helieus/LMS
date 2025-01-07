package com.ilias.library.service;

import com.ilias.library.entity.Role;
import com.ilias.library.entity.User;
import com.ilias.library.repository.RoleRepository;
import com.ilias.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    public List<User> fetchAllUsers() {
        return userRepository.findAll();
    }

    public boolean isUserExists(String username, String email) {
        return userRepository.findByUsername(username) != null || userRepository.findByEmail(email) != null;
    }

    public void assignMemberRole(User user) {
        // Fetch the MEMBER role by name
        Role memberRole = roleRepository.findByName("MEMBER"); // Use the injected roleRepository

        if (memberRole == null) {
            throw new RuntimeException("Role 'MEMBER' not found in the database. Ensure it is inserted.");
        }

        // Assign the MEMBER role to the user
        user.setRoles(Set.of(memberRole));

        // Save the user with the assigned role
        userRepository.save(user);
    }

    public void createUser(User user) {
        // Assign the MEMBER role to the user during registration
        assignMemberRole(user);
        userRepository.save(user);
    }
}
