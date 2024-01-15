package ckb.platform.repositories;

import ckb.platform.entities.Educator;
import ckb.platform.entities.Tournament;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface TournamentRepository extends JpaRepository<ckb.platform.entities.Tournament, Long> {
    @Query("SELECT t.id FROM Tournament t WHERE t.name = :name AND t.subscriptionDeadline = :subscriptionDeadline AND t.creator = :creator")
    long getNewTournamentId(String name, Date subscriptionDeadline, Educator creator);

    @Query("SELECT count(*) FROM Tournament t WHERE t.id = :id AND t.creator = :creator")
    int isTournamentOwner(Long id, Educator creator);

    @Modifying
    @Query("UPDATE Tournament t SET t.endDate = :endDate WHERE t.id = :id AND t.creator = :creator")
    @Transactional
    void closeTournament(Long id, Date endDate, Educator creator);

    @Query("SELECT t.name FROM Tournament t WHERE t.id = :id")
    String getNameById(Long id);
}
