package com.example.demo.services;

import com.example.demo.repo.User;
import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Business layer sitting between the controller and the repository.
 *
 * The star of this example is {@link #addUsers}: it is annotated @Transactional,
 * so all the saves it performs run inside a SINGLE database transaction. If any
 * one save fails (e.g. an invalid user), Spring rolls the whole batch back and
 * NONE of the users are persisted - this is "all or nothing" atomicity.
 */
@Service
public class UserService {

    private final UserRepository repository;

    // Constructor injection (preferred over field @Autowired): dependencies are
    // explicit, the field can be final, and the class is easy to unit-test.
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Saves every user in one transaction. We use Spring's @Transactional
     * (not the Jakarta one) because it lets us tune rollback/propagation if needed.
     *
     * By default Spring rolls back only on unchecked (RuntimeException) errors -
     * the validation failure thrown by Hibernate here is unchecked, so the
     * transaction is rolled back and partial inserts are undone.
     */
    @Transactional
    public void addUsers(List<User> users) {
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

    public void deleteUser(User user) {
        repository.delete(user);
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
