package org.waldreg.repository.teambuilding.teambuilding;

import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.TeamBuilding;

@Repository
public class TeamBuildingRepositoryCommander{

    private final EntityManager entityManager;

    @Autowired
    TeamBuildingRepositoryCommander(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    List<TeamBuilding> readAllTeamBuilding(int startIdx, int endIdx){
        return entityManager.createQuery("select tb from TeamBuilding as tb order by tb.createdAt desc, tb.teamBuildingId desc", TeamBuilding.class)
                .setFirstResult(startIdx - 1)
                .setMaxResults(endIdx - startIdx + 1)
                .getResultList();
    }

}
