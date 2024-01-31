package ckb.platform.repositories;

import ckb.platform.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<ckb.platform.entities.Team, Long> {
    @Query("SELECT t FROM Team t WHERE t.name=:teamName")
    Team getTeamByName(String teamName);
}
