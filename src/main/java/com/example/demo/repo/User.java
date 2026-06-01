package com.example.demo.repo;

// Spring Boot 4 is built on Jakarta EE 11, so persistence/validation
// annotations live under the `jakarta.*` namespace (formerly `javax.*`).
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

/**
 * A JPA entity. Each instance maps to one row in the (auto-generated) "user" table.
 * The Bean Validation annotations (@NotEmpty) are checked both on form binding
 * (controller @Valid) and by Hibernate before an INSERT/UPDATE is flushed.
 */
@Entity
@Table(name = "users") // "user" is a reserved word in some databases, so name the table explicitly
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // let the DB auto-increment the primary key
    private long id;

    @NotEmpty(message = "Name is mandatory")
    private String userName;

    @NotEmpty(message = "Email is mandatory")
    private String email;

    // JPA requires a no-args constructor to instantiate entities when reading from the DB.
    public User() {}

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", userName=" + userName + ", email=" + email + '}';
    }
}
