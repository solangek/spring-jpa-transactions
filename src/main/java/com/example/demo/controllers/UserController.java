package com.example.demo.controllers;

import com.example.demo.repo.User;

import com.example.demo.services.UserService;
import jakarta.validation.Valid; // was javax.validation.Valid before Spring Boot 3/4
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Web layer. Each method maps a URL to a Thymeleaf view name (the .html file
 * under src/main/resources/templates). The two interesting endpoints are
 * /transactionOK and /transactionBAD, which demonstrate transactional rollback.
 */
@Controller
public class UserController {

    private final UserService userService;

    // Constructor injection - Spring passes the UserService bean in automatically.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String main(User user, Model model) {
        model.addAttribute("users", userService.getUsers());
        return "index";
    }

    @GetMapping("/signup")
    public String showSignUpForm(User user, Model model) {
        return "add-user";
    }

    /**
     * Demonstrates a FAILING transaction. We try to add three users, but the third
     * has an empty email which violates @NotEmpty. Because addUsers() is @Transactional,
     * the validation error rolls everything back: John and Ella are NOT saved either.
     */
    @GetMapping("/transactionBAD")
    public String transactionBAD(Model model) {
        List<User> users = new ArrayList<>();
        users.add(new User("John", "john@foo.com"));
        users.add(new User("Ella", "ella@foo.com"));
        users.add(new User("Sandra", "")); // empty email -> validation fails, whole batch rolls back
        try {
            userService.addUsers(users);
        } catch (Exception e) {
            model.addAttribute("message", "Sorry we could not perform your request!");
        } finally {
            model.addAttribute("users", userService.getUsers());
        }
        return "index";
    }

    /**
     * Demonstrates a SUCCESSFUL transaction. All three users are valid, so the
     * single transaction commits and all of them are persisted together.
     */
    @GetMapping("/transactionOK")
    public String transactionOK(Model model) {
        List<User> users = new ArrayList<>();
        users.add(new User("Eli", "eli@foo.com"));
        users.add(new User("Orna", "orna@foo.com"));
        users.add(new User("Ben", "ben@foo.com"));
        try {
            userService.addUsers(users);
        } catch (Exception e) {
            model.addAttribute("message", "Sorry we could not perform your request!");
        } finally {
            model.addAttribute("users", userService.getUsers());
        }
        return "index";
    }

    @PostMapping("/adduser")
    public String addUser(User user, Model model) {
        try {
            userService.saveUser(user);
        } catch (Exception e) {
            model.addAttribute("message", "Sorry we could not perform your request!");
        }
        model.addAttribute("users", userService.getUsers());
        return "index";
    }

    @PostMapping("/edit")
    public String editUser(@RequestParam("id") long id, Model model) {
        User user = userService.getUser(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        // the attribute named "user" is bound to the update-user form
        model.addAttribute("user", user);
        return "update-user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid User user, BindingResult result, Model model) {
        // @Valid triggers Bean Validation; BindingResult collects any violations.
        if (result.hasErrors()) {
            user.setId(id);
            return "update-user";
        }
        userService.saveUser(user);
        model.addAttribute("users", userService.getUsers());
        return "index";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model) {
        User user = userService.getUser(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userService.deleteUser(user);
        model.addAttribute("users", userService.getUsers());
        return "index";
    }

}
