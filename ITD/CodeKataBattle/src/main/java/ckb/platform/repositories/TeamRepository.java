package ckb.platform.repositories;

import ckb.platform.entities.Battle;
import ckb.platform.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<ckb.platform.entities.Team, Long> {
    @Query("SELECT t FROM Team t WHERE t.name=:teamName")
    Team getTeamByName(String teamName);

    @Query("SELECT t FROM Team t WHERE t.battle=:b")
    List<Team> getTeamInBattle(Battle b);
}
