package ckb.platform.repositories;

import ckb.platform.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EducatorRepository extends JpaRepository<ckb.platform.entities.Educator, Long> {
    @Query("SELECT u FROM Educator u WHERE u.email = :email AND u.password = :password")
    User findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("SELECT u FROM Educator u WHERE u.email = :email")
    User alreadyRegistered(@Param("email") String email);
}
