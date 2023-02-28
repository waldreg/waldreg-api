package org.waldreg.repository.teambuilding.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.TeamBuilding;

@Repository
public interface JpaTeamBuildingRepository extends JpaRepository<TeamBuilding, Integer>{

    @Query(value = "SELECT TB.* FROM TEAM_BUILDING AS TB ORDER BY TB.TEAM_BUILDING_CREATED_AT DESC LIMIT :count OFFSET :start", nativeQuery = true)
    List<TeamBuilding> findAll(@Param("start") int start, @Param("count") int count);

}
