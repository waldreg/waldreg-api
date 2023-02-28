package org.waldreg.repository.teambuilding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.TeamUser;

@Repository
public interface JpaTeamUserWrapperRepository extends JpaRepository<TeamUser, Integer>{
}
