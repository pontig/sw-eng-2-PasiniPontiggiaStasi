package ckb.platform.repositories;

import ckb.platform.entities.Battle;
import ckb.platform.entities.Educator;
import ckb.platform.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface BattleRepository extends JpaRepository<Battle, Long> {
    @Query("SELECT b.id FROM Battle b WHERE b.name=:name AND b.registrationDeadline=:registrationDeadline " +
            "AND b.finalSubmissionDeadline=:finalSubmissionDeadline AND b.language=:language AND b.manualEvaluation=:manualEvaluation " +
            "AND b.minStudents=:minStudents AND b.maxStudents=:maxStudents AND b.creator=:creator AND b.tournament=:tournament AND b.hasBeenEvaluated=:hasBeenEvaluated")
    Long getBattleId(String name, Date registrationDeadline, Date finalSubmissionDeadline, String language, boolean manualEvaluation, int minStudents, int maxStudents, Educator creator, Tournament tournament, boolean hasBeenEvaluated);

    @Query("SELECT b FROM Battle b WHERE b.name=:name")
    Battle getBattleByName(String name);
}
