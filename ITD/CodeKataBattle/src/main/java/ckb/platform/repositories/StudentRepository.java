package ckb.platform.repositories;

import ckb.platform.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT u FROM Student u WHERE u.firstName LIKE %:query% OR u.lastName LIKE %:query% OR u.email LIKE %:query%")
    List<Student> findByQuery(String query);

    @Query("SELECT s FROM Student s")
    List<Student> getAllStudentInPlatform();

    @Query("SELECT s FROM Student s WHERE s.email=:email")
    Student getStudentByEmail(String email);
}
