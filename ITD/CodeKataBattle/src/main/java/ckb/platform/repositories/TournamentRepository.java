package ckb.platform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<ckb.platform.entities.Tournament, Long> {
}
