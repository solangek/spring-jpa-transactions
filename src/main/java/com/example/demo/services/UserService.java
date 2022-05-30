package com.example.demo.services;

import com.example.demo.repo.User;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Transactional
    public void addUsers(ArrayList<User> users) {
        for (User user : users) {
            repository.save(user);
        }
    }

    public void saveUser(User user) {
        repository.save(user);
    }
    public void deleteUser(long id) {
        repository.deleteById(id);
    }
    public void deleteUser(User u) {
        repository.delete(u);
    }
    public void updateUser(User user) {
        repository.save(user);
    }
    public Optional<User> getUser(long id) {
        return repository.findById(id);
    }


    public List<User> getUsers() {
        return repository.findAll();
    }
}
