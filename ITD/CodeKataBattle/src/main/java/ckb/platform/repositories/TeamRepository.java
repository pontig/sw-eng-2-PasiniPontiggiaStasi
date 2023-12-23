package ckb.platform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<ckb.platform.entities.Team, Long> {
}
