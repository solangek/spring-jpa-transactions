package com.example.demo.controllers;

import com.example.demo.repo.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String main(User user, Model model) {
        model.addAttribute("users", userService.getUsers());
        return "index";
    }

    @GetMapping("/signup")
    public String showSignUpForm(User user, Model model) {
        return "add-user";
    }
    @GetMapping("/transactionBAD")
    public String transactionBAD(Model model) {
        // we're going to add 3 users but one of them will fail
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("John","john@foo.com"));
        users.add(new User("Ella","ella@foo.com"));
        users.add(new User("Sandra","")); // fails
        try {
            // this transaction will fail and throw an exception
            userService.addUsers(users);
        } catch (Exception e) {
            model.addAttribute("message", "Sorry we could not perform your request!");
        } finally {
            model.addAttribute("users", userService.getUsers());
        }
        return "index";
    }
    @GetMapping("/transactionOK")
    public String transactionOK(Model model) {
        // we're going to add 3 users
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("Eli","eli@foo.com"));
        users.add(new User("Orna","orna@foo.com"));
        users.add(new User("Ben","ben@foo.com"));
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
            model.addAttribute("users", userService.getUsers());

        } catch (Exception e) {
            model.addAttribute("message", "Sorry we could not perform your request!");
        }
        model.addAttribute("users", userService.getUsers());
        return "index";
    }


    @PostMapping("/edit")
    public String editUser(@RequestParam("id") long id, Model model) {

        User user = userService.getUser(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        // the name "user"  is bound to the VIEW
        model.addAttribute("user", user);
        return "update-user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid User user, BindingResult result, Model model) {
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

        User user = userService
                .getUser(id)
                .orElseThrow(
                    () -> new IllegalArgumentException("Invalid user Id:" + id)
                );
        userService.deleteUser(user);
        model.addAttribute("users", userService.getUsers());
        return "index";
    }

}

