package org.waldreg.repository.teambuilding.team.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.Team;

@Repository("teambuildingJpaTeamRepository")
public interface JpaTeamRepository extends JpaRepository<Team, Integer>{

    @Query(value = "SELECT * FROM TEAM WHERE TEAM_BUILDING_TEAM_BUILDING_ID = :teamBuildingId", nativeQuery = true)
    List<Team> findAllByTeamBuildingId(@Param("teamBuildingId") int teamBuildingId);

    @Query(value = "SELECT DISTINCT TU.USER_ID FROM TEAM_USER AS TU INNER JOIN TEAM AS T WHERE T.TEAM_BUILDING_TEAM_BUILDING_ID = :teamBuildingId AND TU.TEAM_TEAM_ID = T.TEAM_TEAM_ID", nativeQuery = true)
    List<Integer> findAllUserIdByTeamBuildingId(@Param("teamBuildingId") int teamBuildingId);

}
