package ckb.platform.repositories;

import ckb.platform.entities.Educator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface TournamentRepository extends JpaRepository<ckb.platform.entities.Tournament, Long> {
    @Query("SELECT t.id FROM Tournament t WHERE t.name = :name AND t.subscriptionDeadline = :subscriptionDeadline AND t.creator = :creator")
    long getNewTournamentId(String name, Date subscriptionDeadline, Educator creator);
}
