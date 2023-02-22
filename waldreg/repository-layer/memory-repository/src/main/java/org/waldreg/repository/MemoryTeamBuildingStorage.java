package org.waldreg.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamBuilding;

@Repository
public class MemoryTeamBuildingStorage{

    private final AtomicInteger atomicInteger;

    private final int startIndex = 0;

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

    public List<TeamBuilding> readAllTeamBuilding(int startIdx, int endIdx){
        int index = startIndex;
        List<TeamBuilding> teamBuildingList = new ArrayList<>();
        for (Map.Entry<Integer, TeamBuilding> teamBuildingEntry : storage.entrySet()){
            if (isInRange(index, startIndex, endIdx)){
                teamBuildingList.add(teamBuildingEntry.getValue());
            }
            index++;
        }
        return teamBuildingList;
    }

    private boolean isInRange(int index, int startIdx, int endIdx){
        return index >= startIdx && index <= endIdx;
    }

    public void deleteTeamBuildingById(int teamBuildingId){
        storage.remove(teamBuildingId);
    }

    public int getTeamBuildingMaxIdx(){
        return storage.size();
    }

    public void addTeamInTeamBuildingTeamList(Team team){
        int teamBuildingId = team.getTeamBuildingId();
        storage.get(teamBuildingId).addTeam(team);
    }

}
