package org.waldreg.repository.teambuilding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waldreg.domain.teambuilding.Team;

public interface JpaTeamRepository extends JpaRepository<Team, Integer>{
}
