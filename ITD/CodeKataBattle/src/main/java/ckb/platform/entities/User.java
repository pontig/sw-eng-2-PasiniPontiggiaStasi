package ckb.platform.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.Objects;
@MappedSuperclass
public abstract class User {
    private @Id @GeneratedValue Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    public User() {}

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }
    public String getPassword() {return this.password;}

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {this.password = password;}

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;
        User user = (User) o;
        return Objects.equals(this.id, user.id) && Objects.equals(this.firstName, user.firstName) && Objects.equals(this.lastName, user.lastName)
                && Objects.equals(this.email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.firstName, this.lastName, this.email);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + this.id + ", firstname='" + this.firstName + '\'' +", lastname='" + this.lastName +  ", email='" + this.email + '\'' + '}';
    }
}
