package org.waldreg.repository.teambuilding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.TeamUser;
import org.waldreg.domain.user.User;

@Repository
public interface JpaTeamUserRepository extends JpaRepository<TeamUser, Integer>{
}
