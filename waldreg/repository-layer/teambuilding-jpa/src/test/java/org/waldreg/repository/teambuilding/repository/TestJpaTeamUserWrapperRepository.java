package org.waldreg.repository.teambuilding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waldreg.domain.teambuilding.TeamUser;

public interface TestJpaTeamUserWrapperRepository extends JpaRepository<TeamUser, Integer>{

}
