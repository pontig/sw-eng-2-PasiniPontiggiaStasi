package ckb.platform.repositories;

import ckb.platform.entities.Educator;
import ckb.platform.entities.Tournament;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EducatorRepository extends JpaRepository<ckb.platform.entities.Educator, Long> {

    @Query("SELECT u FROM Educator u WHERE u.firstName LIKE %:query% OR u.lastName LIKE %:query% OR u.email LIKE %:query%")
    List<Educator> findByQuery(String query);

    @Query("SELECT e FROM Educator e WHERE e.id=:id")
    Educator getEducatorDataById(Long id);

    @Modifying
    @Query("UPDATE Educator e SET e.ownedTournaments =: ownedTournament WHERE e.id =: invitedEDUId")
    @Transactional
    void addOwnedTournament(Long invitedEDUId, List<Tournament> ownedTournament);
}
