package ckb.platform.repositories;

import ckb.platform.entities.Battle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BattleRepository extends JpaRepository<Battle, Long> {
    @Query("SELECT b.id FROM Battle b WHERE b.name=:name")
    Long getBattleIdByName(String name);

    @Query("SELECT b FROM Battle b WHERE b.name=:name")
    Battle getBattleByName(String name);
}
