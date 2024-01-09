package ckb.platform.repositories;

import ckb.platform.entities.Educator;
import ckb.platform.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EducatorRepository extends JpaRepository<ckb.platform.entities.Educator, Long> {

    @Query("SELECT u FROM Educator u WHERE u.firstName LIKE %:query% OR u.lastName LIKE %:query% OR u.email LIKE %:query%")
    List<Educator> findByQuery(String query);
}
