package ckb.platform.entities;

import jakarta.persistence.*;

import java.util.Objects;
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_Edu", discriminatorType = DiscriminatorType.INTEGER)
public abstract class User {

    @Column(name = "id", nullable = false)
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "is_Edu", insertable = false, updatable = false)
    private boolean is_Edu;
    @Column(name = "home_uni")
    private String home_uni;
    public User() {}

    public User( String firstName, String lastName, String email, String password, boolean is_Edu, String home_uni) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.is_Edu = is_Edu;
        this.home_uni = home_uni;
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
    public boolean isEdu() {
        return this.is_Edu;
    }

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
