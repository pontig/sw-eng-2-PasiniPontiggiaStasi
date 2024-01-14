package ckb.platform.repositories;

import ckb.platform.entities.Student;

import ckb.platform.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT u FROM Student u WHERE u.firstName LIKE %:query% OR u.lastName LIKE %:query% OR u.email LIKE %:query%")
    List<Student> findByQuery(String query);

    @Query("SELECT s FROM Student s")
    List<Student> getAllStudentInPlatform();
}
