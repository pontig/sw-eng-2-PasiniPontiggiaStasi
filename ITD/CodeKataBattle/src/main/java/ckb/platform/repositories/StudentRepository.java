package ckb.platform.repositories;

import ckb.platform.entities.Student;

import ckb.platform.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT u FROM Student u WHERE u.email = :email AND u.password = :password")
    Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("SELECT u FROM Student u WHERE u.email = :email")
    Optional<User> alreadyRegistered(@Param("email") String email);

    @Query("SELECT u FROM Student u WHERE u.firstName LIKE %:query% OR u.lastName LIKE %:query% OR u.email LIKE %:query%")
    List<Student> findByQuery(String query);
}
