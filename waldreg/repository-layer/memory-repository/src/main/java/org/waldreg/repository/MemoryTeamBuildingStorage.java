package org.waldreg.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.TeamBuilding;

@Repository
public class MemoryTeamBuildingStorage{

    private final AtomicInteger atomicInteger;

    private final Map<Integer, TeamBuilding> storage;

    {
        atomicInteger = new AtomicInteger(1);
        storage = new ConcurrentHashMap<>();
    }

    public TeamBuilding createTeamBuilding(TeamBuilding teamBuilding){
        teamBuilding.setTeamBuildingId(atomicInteger.getAndIncrement());
        storage.put(teamBuilding.getTeamBuildingId(), teamBuilding);
        return teamBuilding;
    }

    public void updateTeamBuilding(TeamBuilding teamBuilding){
        storage.replace(teamBuilding.getTeamBuildingId(), teamBuilding);
    }

    public void deleteAllTeamBuilding(){
        storage.clear();
    }

    public TeamBuilding readTeamBuildingById(int teamBuildingId){
        return storage.get(teamBuildingId);
    }

}
