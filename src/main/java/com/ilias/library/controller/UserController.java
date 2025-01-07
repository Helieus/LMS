package com.ilias.library.controller;

import com.ilias.library.entity.Role;
import com.ilias.library.entity.User;
import com.ilias.library.service.MemberService;
import com.ilias.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class UserController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String showForm() {
        return "landing"; // Ensure landing.html exists
    }

    @PostMapping("/dologin")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          ModelMap model,
                          HttpSession session) {
        User user = userService.login(username, password);
        if (user == null) { // Invalid credentials
            model.addAttribute("message", "Invalid username or password. Try again.");
            return "landing";
        }

        // Store the logged-in user in the session
        session.setAttribute("loggedInUser", user);

        // Check the user's roles and redirect accordingly
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        if (isAdmin) {
            return "redirect:/admin-dashboard";
        } else {
            return "redirect:/user-dashboard";
        }
    }

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard(HttpSession session, ModelMap model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRoles().stream().noneMatch(role -> role.getName().equalsIgnoreCase("ADMIN"))) {
            return "redirect:/"; // Redirect to login if not an admin
        }
        model.addAttribute("user", user);
        return "admin-dashboard";
    }

    @GetMapping("/user-dashboard")
    public String showUserDashboard(HttpSession session, ModelMap model, Model modell) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRoles().stream().noneMatch(role -> role.getName().equalsIgnoreCase("MEMBER"))) {
            return "redirect:/"; // Redirect to login if not a member
        }
        model.addAttribute("user", user);
        // Example: Check if the user has borrowed a book
        LocalDate today = LocalDate.now();
        boolean hasBorrowedBook = memberService.hasBorrowedBook(user.getId());
        if (hasBorrowedBook) {
            LocalDate dueDate = today.plusDays(5);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            String dueDateFormatted = dueDate.format(formatter);
            model.addAttribute("borrowMessage", "You have until " + dueDateFormatted + " to return the book.");
        }
        return "user-dashboard"; // Ensure user-dashboard.html exists
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // Ensure register.html exists
    }

    @PostMapping("/doregister")
    public String doRegister(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             @RequestParam String email,
                             ModelMap model) {
        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match.");
            return "register";
        }

        // Check if username or email already exists
        if (userService.isUserExists(username, email)) {
            model.addAttribute("message", "Username or email already taken.");
            return "register";
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // Plain text password for simplicity
        user.setEmail(email);
        user.setActive(true);

        // Assign the "MEMBER" role (user_role = 2)
        userService.assignMemberRole(user);

        model.addAttribute("message", "Registration successful. Please log in.");
        return "landing";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidate the session
        session.invalidate();
        return "redirect:/"; // Redirect to login page
    }

    @GetMapping("/getallusers")
    public String getAllUsers(ModelMap model) {
        model.addAttribute("users", userService.fetchAllUsers());
        return "allusers"; // Ensure allusers.html exists
    }
}
